export default interface User {
  email: string;
  emailVerified: boolean;
  phoneNumber?: string;
  phoneNumberVerified?: boolean;
  sub: string;
}
