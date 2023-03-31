package digital.rbq.module.impl.visuals.hud.impl.tabgui.handling;

import com.google.common.collect.Sets;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import digital.rbq.module.impl.visuals.hud.impl.tabgui.TabHandler;
import digital.rbq.module.impl.visuals.hud.impl.tabgui.block.TabBlock;
import digital.rbq.module.impl.visuals.hud.impl.tabgui.handling.handlers.ModCategoryTabFactory;
import digital.rbq.module.impl.visuals.hud.impl.tabgui.handling.handlers.ModuleTabFactory;
import digital.rbq.module.impl.visuals.hud.impl.tabgui.handling.handlers.OptionTabFactory;
import digital.rbq.module.impl.visuals.hud.impl.tabgui.tab.Tab;
import digital.rbq.utils.factory.ClassFactory;
import digital.rbq.utils.factory.exception.FactoryException;

public class Tabs {
	private static Set factoryRegistry = Sets.newHashSet();

	private Tabs() {
	}

	private static void registerFactory(Class handler) {
		try {
			TabFactory factory = (TabFactory) ClassFactory.create(handler, new Object[0]);
			factoryRegistry.add(factory);
		} catch (FactoryException var2) {
			var2.printStackTrace();
		}

	}

	private static TabFactory getFactoryFor(Class clazz) {
		Iterator var1 = factoryRegistry.iterator();

		TabFactory factory;
		do {
			if (!var1.hasNext()) {
				System.err.println("NO FACTORY FOUND FOR CLASS: " + clazz);
				throw new UnsupportedOperationException("There is no handler for the object: " + clazz.getSimpleName());
			}

			factory = (TabFactory) var1.next();
		} while (!factory.getHandledType().isAssignableFrom(clazz));

		return factory;
	}

	public static Tab newTab(TabHandler handler, Object stateObject, TabBlock container) {
		return newTab(handler, stateObject, (Tab) null, (TabBlock) null, container);
	}

	public static Tab newTab(TabHandler handler, Object stateObject, Tab parent, TabBlock container) {
		return newTab(handler, stateObject, parent, (TabBlock) null, container);
	}

	public static Tab newTab(TabHandler handler, Object stateObject, TabBlock children, TabBlock container) {
		return newTab(handler, stateObject, (Tab) null, children, container);
	}

	public static Tab newTab(TabHandler handler, Object stateObject, Tab parent, TabBlock children,
			TabBlock container) {
		return getFactoryFor(stateObject.getClass()).parse(handler, stateObject, parent, children, container);
	}

	public static TabBlock newTabBlock() {
		return new TabBlock();
	}

	public static <T> Collector<T, TabBlock, TabBlock> toTabBlock(final TabHandler handler) {
		return new Collector<T, TabBlock, TabBlock>() {

			@Override
			public Supplier<TabBlock> supplier() {
				return TabBlock::new;
			}

			@Override
			public BiConsumer<TabBlock, T> accumulator() {
				return (block, t) -> block.appendTab(Tabs.newTab(handler, t, block));
			}

			@Override
			public BinaryOperator<TabBlock> combiner() {
				return (left, right) -> {
					right.forEach(left::appendTab);
					return left;
				};
			}

			@Override
			public Function<TabBlock, TabBlock> finisher() {
				return Function.identity();
			}

			@Override
			public Set<Collector.Characteristics> characteristics() {
				return EnumSet.of(Collector.Characteristics.UNORDERED);
			}
		};
	}

	static {
		registerFactory(OptionTabFactory.class);
		registerFactory(ModCategoryTabFactory.class);
		registerFactory(ModuleTabFactory.class);
	}
}
