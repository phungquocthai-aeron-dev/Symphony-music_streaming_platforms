export interface Authentication {
    phone: string,
    password: string
}

export interface AuthenticationResponse {
    authenticated: boolean,
    token: string
}