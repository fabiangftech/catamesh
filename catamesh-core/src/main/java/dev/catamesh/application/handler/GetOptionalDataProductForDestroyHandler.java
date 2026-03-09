package dev.catamesh.application.handler;

import dev.catamesh.core.cqrs.Query;
import dev.catamesh.core.handler.DestroyDataProductContext;
import dev.catamesh.core.handler.Handler;
import dev.catamesh.core.model.DataProduct;

import java.util.Optional;

public class GetOptionalDataProductForDestroyHandler extends Handler<DestroyDataProductContext> {

    private final Query<String, Optional<DataProduct>> optionalDataProductQuery;

    public GetOptionalDataProductForDestroyHandler( Query<String, Optional<DataProduct>> optionalDataProductQuery) {
        this.optionalDataProductQuery = optionalDataProductQuery;
    }

    @Override
    protected void doHandle(DestroyDataProductContext context) {
        optionalDataProductQuery.execute(context.getName())
                .ifPresent(context::setDataProduct);
    }
}
