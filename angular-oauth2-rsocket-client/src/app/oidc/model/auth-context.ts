import { UserProfile } from './user-profile';

const ROLES = {
  ADMIN_IHM: 'ADMIN',
  OTHER: 'LISTING'
}

export class AuthContext {
  userProfile: UserProfile;
  targetAudience: string[];

  get isAdmin() {
    return this.userProfile.userPermissions.find(p => p.role === ROLES.ADMIN_IHM);
  }
}