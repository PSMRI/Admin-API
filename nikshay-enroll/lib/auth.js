/**
 * Ni-kshay SSO login via Playwright (headless by default).
 */

function randomCaptcha() {
  return Math.random().toString(36).slice(2, 8).toUpperCase()
}

async function resolveCaptcha(page) {
  const codeEl = page.locator('#capText')
  if ((await codeEl.count()) === 0) return randomCaptcha()
  for (let i = 0; i < 25; i++) {
    const code = ((await codeEl.textContent().catch(() => '')) || '').trim()
    if (code.length >= 4) return code
    await page.waitForTimeout(300)
  }
  return randomCaptcha()
}

async function pageCanHashPassword(page, timeoutMs) {
  if ((await page.locator('input[name="isEncryptedData"][value="true"]').count()) === 0) return true
  return page
    .waitForFunction(
      () => {
        const $ = window.jQuery
        if (typeof $ !== 'function' || !window.forge || typeof window.md5 !== 'function') return false
        const button = $('.loginButton')[0]
        return Boolean(button && $._data?.(button, 'events')?.click?.length)
      },
      undefined,
      { timeout: timeoutMs }
    )
    .then(() => true)
    .catch(() => false)
}

async function submitWithOwnHashing(page, username, password) {
  const hash = await page.evaluate(async (args) => {
    const clientId = document.querySelector('input[name="clientId"]')?.value
    if (!clientId) return 'ERR:the login page is missing its clientId field'
    const base = location.origin
    const idHeader = { 'X-SSO-Client-Id': clientId }

    const tokenRes = await (
      await fetch(`${base}/v1/ssoserver/client`, { credentials: 'include', headers: idHeader })
    ).json()
    const authToken = tokenRes?.data?.authToken
    if (!authToken) return 'ERR:the portal did not issue an auth token'

    const salt = await (
      await fetch(`${base}/v1/user/saltKey?username=${encodeURIComponent(args.user)}`, {
        credentials: 'include',
        headers: { ...idHeader, 'X-SSO-Auth-Token': authToken }
      })
    ).json()
    if (!salt?.success || !salt.data) return 'ERR:the portal did not return a salt for this username'

    const tokenRes2 = await (
      await fetch(`${base}/v1/ssoserver/client`, { credentials: 'include', headers: idHeader })
    ).json()
    const sessionRes = await (
      await fetch(`${base}/v1/ssoserver/session-key`, {
        credentials: 'include',
        headers: { ...idHeader, 'X-SSO-Auth-Token': tokenRes2?.data?.authToken ?? '' }
      })
    ).json()
    if (!sessionRes?.success || !sessionRes.data) return 'ERR:the portal did not return a session key'

    let digest = ''
    for (let round = 0; round < 2; round++) {
      const input = round === 0 ? salt.data + args.pass : sessionRes.data + digest
      const bytes = new Uint8Array(
        await crypto.subtle.digest('SHA-256', new TextEncoder().encode(input))
      )
      digest = Array.from(bytes)
        .map((b) => b.toString(16).padStart(2, '0'))
        .join('')
    }
    return digest
  }, { user: username, pass: password })

  if (hash.startsWith('ERR:')) return hash.slice(4)

  await page
    .evaluate((hashed) => {
      const pw = document.querySelector('#password')
      const external = document.querySelector('#isExternalLogin')
      if (external) external.value = 'false'
      if (!pw) return
      pw.value = hashed
      pw.form?.submit()
    }, hash)
    .catch(() => undefined)
  return null
}

/**
 * When the user already has a portal session elsewhere (another browser, or another run),
 * the portal shows "You are already logged in. Click here to go to Overview Page" instead
 * of the dashboard. Follows that link. Returns true when the page was that one and the
 * portal opened afterwards.
 */
async function continueIfAlreadyLoggedIn(page) {
  const text = (await page.locator('body').innerText({ timeout: 3_000 }).catch(() => '')) || ''
  if (!/already logged in/i.test(text)) return false
  const link = page.getByText(/click here/i).first()
  if ((await link.count()) === 0) return false
  await link.click()
  await page.waitForLoadState('domcontentloaded').catch(() => undefined)
  await page.waitForTimeout(2_000)
  const url = new URL(page.url())
  // Back on the SSO login page: the click did not open the portal
  return !url.hostname.startsWith('sso.') && !/already logged in/i.test(
    (await page.locator('body').innerText({ timeout: 3_000 }).catch(() => '')) || '')
}

/** Log in once. Throws on failure. */
export async function login(page, username, password, loginUrl, homeUrlGlob) {
  const alerts = []
  const onDialog = (d) => {
    alerts.push(d.message())
    void d.dismiss().catch(() => undefined)
  }
  page.on('dialog', onDialog)

  try {
    await page.goto(loginUrl, { waitUntil: 'domcontentloaded' })
    const pageHashes = await pageCanHashPassword(page, 8_000)

    await page.getByRole('textbox', { name: 'User Name' }).fill(username)
    await page.getByRole('textbox', { name: 'Password' }).fill(password)

    const captcha = page.getByRole('textbox', { name: 'captcha' })
    if ((await captcha.count()) > 0) {
      await captcha.first().fill(await resolveCaptcha(page))
    }

    if (pageHashes) {
      await page.getByRole('button', { name: 'Login' }).click()
    } else {
      const failedStep = await submitWithOwnHashing(page, username, password)
      if (failedStep) throw new Error(`Login failed: ${failedStep}.`)
    }

    try {
      await page.waitForURL(homeUrlGlob, { timeout: 20_000 })
    } catch {
      if (await continueIfAlreadyLoggedIn(page)) return
      const msg =
        (await page.locator('#setError').first().textContent().catch(() => null)) ||
        (await page
          .locator('text=/invalid|incorrect|wrong|failed|error/i')
          .first()
          .textContent()
          .catch(() => null))
      const detail = [msg?.trim(), alerts.length ? `page alert: ${alerts.join('; ')}` : '']
        .filter(Boolean)
        .join(' — ')
      throw new Error(
        detail
          ? `Login failed: ${detail}`
          : 'Login failed: did not reach the dashboard. Check username/password.'
      )
    }
  } finally {
    page.off('dialog', onDialog)
  }
}
