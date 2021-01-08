export class UserRequest {
  firstName: string;
  lastName: string;
  username: string;
  email: string;
  role: string;
  notLocked: boolean;
  active: boolean;
  profileImage: File;

  constructor() {
    this.firstName = '';
    this.lastName = '';
    this.username = '';
    this.email = '';
    this.role = '';
    this.notLocked = false;
    this.active = false;
    this.profileImage = null;
  }
}
