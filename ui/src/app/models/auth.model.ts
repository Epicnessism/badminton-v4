export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  userId: string;
  username: string;
  givenName: string;
  familyName: string;
}

export interface AuthUser {
  userId: string;
  username: string;
  givenName: string;
  familyName: string;
}
