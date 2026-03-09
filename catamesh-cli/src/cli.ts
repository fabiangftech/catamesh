#!/usr/bin/env node

import { InitQuery } from "./infrastructure/cqrs/InitQuery";

const args = process.argv.slice(2);

if (args[0] !== "new") {
  process.stderr.write("Usage: cata new <args>\n");
  process.exit(1);
}

try {
  const output = new InitQuery().execute(args);
  process.stdout.write(output);
} catch (error) {
  const message = error instanceof Error ? error.message : String(error);
  process.stderr.write(message + "\n");
  process.exit(1);
}