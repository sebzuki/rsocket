import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {HomeComponent} from './home/home.component';
import {SigninRedirectCallbackComponent} from './oidc/signin-redirect-callback.component';
import {SignoutRedirectCallbackComponent} from './oidc/signout-redirect-callback.component';
import {UnauthorizedComponent} from './oidc/unauthorized.component';
import {PageNotFoundComponent} from './page-not-found.component';


const routes: Routes = [
    { path: '', component: UnauthorizedComponent },
    { path: 'home', component: HomeComponent },
    { path: 'signin-callback', component: SigninRedirectCallbackComponent },
    { path: 'signout-callback', component: SignoutRedirectCallbackComponent },
    { path: '**', component: PageNotFoundComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
