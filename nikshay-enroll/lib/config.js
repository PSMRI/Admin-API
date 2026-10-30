/** UAT (beta) is the default. Pass `production` as last CLI arg to switch. */
export const ENV = {
  uat: {
    loginUrl:
      'https://sso.beta.nikshay.in/v1/sso/login?returnUrl=https://beta.nikshay.in/Home&clientId=29',
    homeUrlGlob: '**/beta.nikshay.in/Home',
    origin: 'https://beta.nikshay.in'
  },
  production: {
    loginUrl:
      'https://sso.nikshay.in/v1/sso/login?returnUrl=https://www.nikshay.in/Home&clientId=29',
    homeUrlGlob: '**/www.nikshay.in/Home',
    origin: 'https://www.nikshay.in'
  }
}

export const DEFAULT_ENV = 'uat'
