package ru.org.adons.cblock.app;

import dagger.Component;
import ru.org.adons.cblock.data.BlockListModelTest;
import ru.org.adons.cblock.data.CallLogModelTest;
import ru.org.adons.cblock.ui.base.ViewScope;

/**
 * Provide Application and data-model dependencies for test
 */
@ViewScope
@Component(dependencies = ApplicationComponent.class)
public interface TestComponent {

    void inject(CallLogModelTest test);

    void inject(BlockListModelTest test);

}
