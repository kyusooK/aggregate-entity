package aggregateentity.infra;

import aggregateentity.domain.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class OrderHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Order>> {

    @Override
    public EntityModel<Order> process(EntityModel<Order> model) {
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/placeorder")
                .withRel("placeorder")
        );
        model.add(
            Link
                .of(model.getRequiredLink("self").getHref() + "/modifyorder")
                .withRel("modifyorder")
        );

        return model;
    }
}
