import { Routes } from '@angular/router';
import { HomeComponent } from './features/home/home.component';
import { LoginComponent } from './features/auth/login/login.component';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { RegisterComponent } from './features/auth/register/register.component';
import { LoginGuard } from './core/guards/login.guard';
import { AuthGuard } from './core/guards/auth.guard';
import { SingerComponent } from './features/singer/singer.component';
import { SongComponent } from './features/song/song.component';
import { TopicComponent } from './features/topic/topic.component';
import { ReleaseComponent } from './features/release/release.component';
import { RecentComponent } from './features/recent/recent.component';
import { FavoriteComponent } from './features/favorite/favorite.component';
import { SearchComponent } from './features/search/search.component';
import { RankComponent } from './features/rank/rank.component';
import { UpgradeComponent } from './features/upgrade/upgrade.component';
import { ClauseComponent } from './features/clause/clause.component';
import { IntroduceComponent } from './features/introduce/introduce.component';
import { ErrorComponent } from './features/error/error.component';
import { SimpleLayoutComponent } from './layouts/simple_layout/simple_layout.component';
import { UserComponent } from './features/user/user.component';
import { StatisticComponent } from './features/admin/statistic/statistic.component';
import { UsersComponent } from './features/admin/users/users.component';
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import { LibraryComponent } from './features/library/library.component';

export const routes: Routes = [
    { 
        path: '',
        component: MainLayoutComponent,
        children: [
            {path: '', redirectTo: 'home', pathMatch: 'full' },
            {path: 'home', component: HomeComponent},
            {path: 'singer/:id', component: SingerComponent},
            {path: 'song/:id', component: SongComponent},
            {path: 'topic', component: TopicComponent},
            {path: 'recent', component: RecentComponent},
            {path: 'hot', component: ReleaseComponent},
            {path: 'favorite', component: FavoriteComponent},
            {path: 'search', component: SearchComponent},
            {path: 'ranking', component: RankComponent},
            {path: 'user', component: UserComponent},
            {path: 'library', component: LibraryComponent}
        ]
    },
    {
        path: '',
        component: SimpleLayoutComponent,
        children: [
            { path: 'upgrade', component: UpgradeComponent },
            { path: 'clause', component: ClauseComponent },
            { path: 'introduce', component: IntroduceComponent },
        ]
    },
    {
        path: 'admin',
        component: AdminLayoutComponent,
        children: [
            { path: '', component: StatisticComponent },
            { path: 'users', component: UsersComponent},
        ]
    },
    { path: 'login', component: LoginComponent, canActivate: [LoginGuard]},
    { path: 'register', component: RegisterComponent },

    { 
        path: '',
        component: SimpleLayoutComponent,
        children: [
            { path: '**', component: ErrorComponent }
        ]
    }
];
