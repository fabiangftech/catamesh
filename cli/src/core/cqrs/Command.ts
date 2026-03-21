export interface Command<I, O> {

    execute(input: I): O;
}