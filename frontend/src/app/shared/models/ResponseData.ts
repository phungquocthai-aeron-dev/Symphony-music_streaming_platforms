export type ResponseData<D> = {
    result: D;
    message?: string;
    code: number;
};