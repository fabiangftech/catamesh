package dev.catamesh.application.facade;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.facade.TemplateFacade;

public class DefaultTemplateFacade implements TemplateFacade {

    private final Query<String, String> getFileFromResourceQuery;

    public DefaultTemplateFacade(Query<String, String> getFileFromResourceQuery) {
        this.getFileFromResourceQuery = getFileFromResourceQuery;
    }

    @Override
    public String initDataProduct(String dataProductName) {
        String yaml = getFileFromResourceQuery.execute("templates/data-product.v1.template.yaml");
        return yaml.replace("${NAME}", dataProductName);
    }
}
