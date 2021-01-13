import { Injectable } from '@angular/core';
import {
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpInterceptor,
} from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthenticationService } from '../service/authentication.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private authService: AuthenticationService) {}

  intercept(
    request: HttpRequest<any>,
    httpHandler: HttpHandler
  ): Observable<HttpEvent<unknown>> {
    if (request.url.includes(`${this.authService.host}/api/user/login`)) {
      return httpHandler.handle(request);
    }
    if (request.url.includes(`${this.authService.host}/api/user/register`)) {
      return httpHandler.handle(request);
    }
    if (
      request.url.includes(`${this.authService.host}/api/user/resetpassword`)
    ) {
      return httpHandler.handle(request);
    }
    this.authService.loadToken();

    const token = this.authService.getToken();

    const req = request.clone({
      setHeaders: { Authorization: 'Bearer ${token}' },
    });

    return httpHandler.handle(req);
  }
}
