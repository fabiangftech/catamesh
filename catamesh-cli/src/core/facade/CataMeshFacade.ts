interface CataMeshFacade {
    init(command: string[]): void;
    diff(command: string[]): void;
    plan(command: string[]): void;
    apply(command: string[]): void;
}