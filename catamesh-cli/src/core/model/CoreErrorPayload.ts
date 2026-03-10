export interface CoreErrorPayload {
    errorCode: string;
    status: number;
    title: string;
    message: string;
    hint?: string;
    details?: string[];
}
