# Ni-kshay login worker (Admin-API)

Node + Playwright worker that logs in to the Ni-kshay portal in a real browser and
prints the portal's session cookies as JSON. Admin-API (`NikshayAuthClient`) runs it
before every Ni-kshay phase run (ENROLMENT, CONTACTS, NAAT_UPLOAD, XRAY_UPLOAD) and for
`POST /stopTb/nikshay/testLogin`. All other Ni-kshay calls are made by Admin-API itself.

Files:

- `login-json.js`: the worker Admin-API runs
- `lib/auth.js`: the login steps on the portal page
- `lib/config.js`: the portal URLs for `uat` (beta.nikshay.in) and `production`

## Setup (on the machine that runs Admin-API)

```bash
cd nikshay-enroll
npm install
```

Requires Node.js and Chromium (installed via the Playwright postinstall).

Config in the Admin-API properties (defaults shown):

```
nikshay.enroll.dir=nikshay-enroll
nikshay.enroll.node=node
nikshay.login.timeout-ms=180000
nikshay.portal.env=uat
```

`nikshay.enroll.dir` is resolved from the Admin-API working directory.

## Run by hand

```bash
NIKSHAY_USER=... NIKSHAY_PASS=... node login-json.js --env uat
```

The output is a live login: never log it or share it.
