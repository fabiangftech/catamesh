#!/usr/bin/env node

import { InitQuery } from "./infrastructure/cqrs/InitQuery";

interface InitCommandQuery {
  execute(args: string[]): string;
}

interface WritableStream {
  write(chunk: string): unknown;
}

const SUCCESS = 0;
const FAILURE = 1;

export const runCli = (
  args: string[],
  initQuery: InitCommandQuery = new InitQuery(),
  stdout: WritableStream = process.stdout,
  stderr: WritableStream = process.stderr,
): number => {
  if (args[0] !== "new") {
    stderr.write("Usage: cata new <args>\n");
    return FAILURE;
  }

  try {
    const output = initQuery.execute(args.slice(1));
    stdout.write(output);
    return SUCCESS;
  } catch (error) {
    const message = error instanceof Error ? error.message : String(error);
    stderr.write(message.endsWith("\n") ? message : `${message}\n`);
    return FAILURE;
  }
};

if (require.main === module) {
  process.exit(runCli(process.argv.slice(2)));
}
