#!/usr/bin/env node

const command = process.argv[2];

const printUsage = (): void => {
  console.error("Uso: cata [plan|apply]");
};

if (!command) {
  console.log("CataMesh CLI listo. Usa 'cata plan' o 'cata apply'.");
  process.exit(0);
}

switch (command) {
  case "plan":
    console.log("cata plan (placeholder): comando disponible.");
    process.exit(0);
  case "apply":
    console.log("cata apply (placeholder): comando disponible.");
    process.exit(0);
  default:
    console.error(`Comando desconocido: ${command}`);
    printUsage();
    process.exit(1);
}
