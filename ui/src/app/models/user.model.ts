export interface CreateUserRequest {
  givenName: string;
  familyName: string;
  email: string;
  username: string;
  age: number;
  birthday: string;
  password: string;
}

export interface User {
  userId: string;
  givenName: string;
  familyName: string;
  email: string;
  username: string;
  age: number;
  birthday: string;
  createdAt: string;
}
