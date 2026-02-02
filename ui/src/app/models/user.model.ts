export interface CreateUserRequest {
  givenName: string;
  familyName: string;
  email: string;
  username: string;
  birthday: string;
  password: string;
  isStringer?: boolean;
}

export interface UpdateUserRequest {
  givenName?: string;
  familyName?: string;
  email?: string;
  birthday?: string;
  password?: string;
}

export interface User {
  userId: string;
  givenName: string;
  familyName: string;
  email: string;
  username: string;
  birthday: string;
  createdAt: string;
}
