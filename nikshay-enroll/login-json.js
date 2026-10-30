/**
 * CLI for Admin-API (NikshayAuthClient): log in to Ni-kshay in a real browser and
 * print the portal's session cookies as ONE JSON object on stdout, so Java can
 * make the portal API calls itself.
 *
 *   NIKSHAY_USER=… NIKSHAY_PASS=… node login-json.js --env uat|production
 *
 * stdout on success:  { "origin": "https://beta.nikshay.in", "cookies": [ { "name", "value" }, … ] }
 * stdout on failure:  { "error": "…", "rejected": true|false }   (exit code 1)
 *   rejected = the portal refused the login (wrong username/password/captcha);
 *   otherwise the portal couldn't be reached or answered unexpectedly.
 *
 * The cookies are a live login: stdout is read only by Admin-API and must never be
 * logged. Progress goes to stderr and never includes the password or cookies.
 */
import { chromium } from 'playwright'
import { ENV, DEFAULT_ENV } from './lib/config.js'
import { login } from './lib/auth.js'

function arg(name) {
  const i = process.argv.indexOf(`--${name}`)
  return i >= 0 ? process.argv[i + 1] : undefined
}

function fail(message, rejected) {
  console.error(`login failed: ${message}`)
  process.stdout.write(JSON.stringify({ error: message, rejected }))
  process.exitCode = 1
}

const envName = arg('env') || process.env.NIKSHAY_ENV || DEFAULT_ENV
const urls = ENV[envName]
const username = process.env.NIKSHAY_USER
const password = process.env.NIKSHAY_PASS

if (!urls) {
  fail(`unknown env "${envName}" (use uat or production)`, false)
} else if (!username || !password) {
  fail('NIKSHAY_USER and NIKSHAY_PASS are required', true)
} else {
  const headless = process.env.HEADLESS !== '0'
  // Prefer system Google Chrome when present; PLAYWRIGHT_CHROME=0 uses Playwright's Chromium.
  const useSystemChrome = process.env.PLAYWRIGHT_CHROME !== '0'
  let browser
  let page
  try {
    console.error(`login: ${envName} (${urls.origin}) user=${username}`)
    browser = await chromium.launch({ headless, ...(useSystemChrome ? { channel: 'chrome' } : {}) })
    const context = await browser.newContext()
    page = await context.newPage()
    page.setDefaultTimeout(30_000)
    // The login page waits for jQuery/forge/md5 from public CDNs; loading it has taken
    // 2-10s and sometimes more than 30s, so page loads get a longer limit.
    page.setDefaultNavigationTimeout(90_000)
    await login(page, username, password, urls.loginUrl, urls.homeUrlGlob)
    if (!page.url().startsWith(urls.origin)) {
      await page.goto(`${urls.origin}/Home`, { waitUntil: 'domcontentloaded' })
    }
    const cookies = (await context.cookies(urls.origin)).map((c) => ({ name: c.name, value: c.value }))
    console.error(`login: ok (${cookies.length} cookies)`)
    process.stdout.write(JSON.stringify({ origin: urls.origin, cookies }))
  } catch (e) {
    let message = (e && e.message ? e.message : String(e)).split('\n')[0]
    // Right credentials, but Ni-kshay forces a password change first (first login or
    // expired password). Never changed automatically: someone has to do it by hand.
    if (page && new URL(page.url()).pathname.endsWith('/resetPassword')) {
      message = 'Login failed: Ni-kshay requires a password change for this login. Log in to the portal by hand, ' +
        'set a new password, then update it in the device configuration.'
    }
    // lib/auth.js throws "Login failed: …" when the portal refuses the login
    fail(message + (page ? await whereItStopped(page) : ''), message.startsWith('Login failed'))
  } finally {
    if (browser) await browser.close().catch(() => undefined)
  }
}

/**
 * Where the browser ended up after a failed login: URL without the query, page
 * title and the start of the visible text (e.g. a 403 or error page). Visible
 * text never includes what was typed into the password field.
 */
async function whereItStopped(page) {
  try {
    const url = new URL(page.url())
    const title = (await page.title().catch(() => '')).trim()
    const text = ((await page.locator('body').innerText({ timeout: 3_000 }).catch(() => '')) || '')
      .replace(/\s+/g, ' ')
      .trim()
      .slice(0, 300)
    return ` [ended at ${url.origin}${url.pathname}; title "${title}"; page text: "${text}"]`
  } catch {
    return ''
  }
}
