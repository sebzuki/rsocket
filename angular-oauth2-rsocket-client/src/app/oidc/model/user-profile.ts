import { UserPermission } from "./user-permission";

export interface UserProfile {
    login: string;
    id: string;
    email: string;
    firstName: string;
    lastName: string;
    userPermissions: UserPermission[];
}