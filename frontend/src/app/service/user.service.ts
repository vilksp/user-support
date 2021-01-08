import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpErrorResponse,
  HttpEvent,
  HttpResponse,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../model/User';
import { JwtHelperService } from '@auth0/angular-jwt';
import { environment } from '../../environments/environment';
import { UserRequest } from '../model/UserRequest';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private host = environment.apiUrl;
  constructor(private http: HttpClient) {}

  public getUsers(): Observable<User[] | HttpErrorResponse> {
    return this.http.get<User[]>(`{$this.host}/api/user/list`);
  }
  public addUser(
    userRequest: UserRequest
  ): Observable<User | HttpErrorResponse> {
    return this.http.post(`${this.host}/api/user/add`, userRequest);
  }

  public updateUser(
    userRequest: UserRequest,
    formData: FormData
  ): Observable<User | HttpErrorResponse> {
    return this.http.post(`${this.host}/api/user/update`, userRequest);
  }
  public resetPassword(email: string): Observable<any | HttpErrorResponse> {
    return this.http.get(`${this.host}/api/user/resetpassword/${email}`);
  }

  public updateProfileImage(
    formData: FormData
  ): Observable<HttpEvent<any> | HttpErrorResponse> {
    return this.http.post<User>(
      `${this.host}/api/user/updateProfileImage`,
      formData,
      { reportProgress: true, observe: 'events' }
    );
  }

  public deleteUser(userId: number): Observable<any | HttpErrorResponse> {
    return this.http.delete(`$this.host}/api/user/delete/${userId}`);
  }

  public addUsersToLocalCache(users: User[]): void {
    localStorage.setItem('users', JSON.stringify(users));
  }

  public getUsersFromLocalStorage(): User[] {
    if (localStorage.getItem('users')) {
      return JSON.parse(localStorage.getItem('users'));
    }
    return null;
  }
  public createUserForm(): User {
    if (localStorage.getItem('users')) {
      return JSON.parse(localStorage.getItem('users'));
    }
    return null;
  }
}
