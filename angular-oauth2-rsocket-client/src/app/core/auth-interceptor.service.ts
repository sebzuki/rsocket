import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { mergeMap, tap } from 'rxjs/operators';
import { AuthService } from './auth-service.component';

@Injectable({
    providedIn: 'root'
})
export class AuthInterceptorService implements HttpInterceptor {
    constructor(private _authService: AuthService,
                private _router: Router) {
    }

    intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return this._authService.getAccessToken().pipe(
            mergeMap(token => {
                return next.handle(
                    req.clone({
                        setHeaders: {
                            'Authorization': `Bearer ${token}`
                        }
                    })
                )
            }),
            tap(response => {
                },
                error => {
                    var respError = error as HttpErrorResponse;
                    if (respError && (respError.status === 401 || respError.status === 403)) {
                        this._router.navigateByUrl('/unauthorized');
                    }
                })
        );
    }
}
