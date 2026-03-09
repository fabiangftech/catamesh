import {Command} from "../../core/cqrs/Command";
import {DataProduct} from "../../core/model/DataProduct";
import {ColorConfig} from "../config/ColorConfig";

export class DataProductPrintCommand implements Command<DataProduct, void>{
    execute(input: DataProduct): void {
        console.log(`${ColorConfig.cyan}ID:${ColorConfig.reset} ${ColorConfig.white}${input.metadata.id}${ColorConfig.reset}`);
        console.log(`${ColorConfig.cyan}Schema Version:${ColorConfig.reset} ${ColorConfig.white}${input.schemaVersion}${ColorConfig.reset}`);
        console.log(`${ColorConfig.cyan}Data Product:${ColorConfig.reset} ${ColorConfig.white}${input.metadata.name}${ColorConfig.reset}`);
        console.log(`${ColorConfig.cyan}Display Name:${ColorConfig.reset} ${ColorConfig.white}${input.metadata.displayName}${ColorConfig.reset}`);
        console.log(`${ColorConfig.cyan}Domain:${ColorConfig.reset} ${ColorConfig.white}${input.metadata.domain}${ColorConfig.reset}`);
        console.log(`${ColorConfig.cyan}Description:${ColorConfig.reset} ${ColorConfig.white}${input.metadata.description}${ColorConfig.reset}`);
        console.log(`${ColorConfig.cyan}Kind:${ColorConfig.reset} ${ColorConfig.white}${input.spec.kind}${ColorConfig.reset}`);
        console.log("");
        console.log(`${ColorConfig.cyan}Resources:${ColorConfig.reset}`);

        if (input.spec.resources.length === 0) {
            console.log(` ${ColorConfig.white}(none)${ColorConfig.reset}`);
            return undefined;
        }

        input.spec.resources.forEach((resource, index) => {
            console.log(` ${ColorConfig.green}-${ColorConfig.reset} ${ColorConfig.white}${resource.name}${ColorConfig.reset}`);
            console.log(`   ${ColorConfig.cyan}ID:${ColorConfig.reset} ${ColorConfig.white}${resource.id}${ColorConfig.reset}`);
            console.log(`   ${ColorConfig.cyan}Display Name:${ColorConfig.reset} ${ColorConfig.white}${resource.displayName}${ColorConfig.reset}`);
            console.log(`   ${ColorConfig.cyan}Kind:${ColorConfig.reset} ${ColorConfig.white}${resource.kind}${ColorConfig.reset}`);
            console.log(`   ${ColorConfig.cyan}Data Product ID:${ColorConfig.reset} ${ColorConfig.white}${resource.dataProductId}${ColorConfig.reset}`);
            console.log(`   ${ColorConfig.cyan}Definition:`);
            console.log(`   ${ColorConfig.cyan} Kind:${ColorConfig.reset} ${ColorConfig.white}${resource.definition.schemaVersion}`);
            console.log(`   ${ColorConfig.cyan} Version: ${ColorConfig.white}${resource.definition.version}${ColorConfig.reset}`);
            console.log(`   ${ColorConfig.cyan} Config:${ColorConfig.reset} ${ColorConfig.white}${JSON.stringify(resource.definition.config)}${ColorConfig.reset}`);

            if (index < input.spec.resources.length - 1) {
                console.log("");
            }
        });
        return undefined;
    }
}
