export interface Facade<I,O>{
    run(input: I): O;
}