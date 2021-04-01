import { Permission } from './permission';

export class UserPermission {
    role: string;
    permissions: Permission[];


    constructor(role: string, permissions: Permission[]=[]) {
        this.role = role;
        this.permissions = permissions;
    }
}