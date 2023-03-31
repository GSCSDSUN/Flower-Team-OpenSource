package digital.rbq.module.impl.visuals.hud.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.StreamSupport;
import me.zane.basicbus.api.annotations.Listener;
import net.minecraft.client.gui.ScaledResolution;
import digital.rbq.core.Autumn;
import digital.rbq.module.Module;
import digital.rbq.module.ModuleCategory;
import digital.rbq.module.impl.visuals.hud.Component;
import digital.rbq.module.impl.visuals.hud.HUDMod;
import digital.rbq.module.impl.visuals.hud.impl.tabgui.TabHandler;
import digital.rbq.module.impl.visuals.hud.impl.tabgui.TabInputHandler;
import digital.rbq.module.impl.visuals.hud.impl.tabgui.block.TabBlock;
import digital.rbq.module.impl.visuals.hud.impl.tabgui.cursor.cursors.QueuedTabCursor;
import digital.rbq.module.impl.visuals.hud.impl.tabgui.handling.Tabs;
import digital.rbq.module.impl.visuals.hud.impl.tabgui.tab.Tab;

public final class TabComponent extends Component {
	private final TabHandler handler = TabHandler.builder().renderCoordinates(2, 12).tabDimensions(60, 12)
			.tabSpacing(2, 0).tabCursor(new QueuedTabCursor()).build();
	private boolean loaded;

	public TabComponent(HUDMod parent) {
		super(parent);
		Autumn.EVENT_BUS_REGISTRY.eventBus.subscribe(new TabInputHandler(this));
	}

	private static Tab apply(Object tab) {
		return (Tab) tab;
	}

	@Listener(Integer.class)
	public final void onKeyPress(Integer keyCode) {
		if (this.getParent().tabGui.getValue()) {
			this.handler.doKeyInput(keyCode);
		}

	}

	public TabHandler getHandler() {
		return this.handler;
	}

	private void setupTabs() {
		Map categoryMappings = new LinkedHashMap(ModuleCategory.values().length);
		TabBlock workingBlock = Tabs.newTabBlock();
		Arrays.stream(ModuleCategory.values()).map((category) -> {
			return Tabs.newTab(this.handler, category, Tabs.newTabBlock(), workingBlock);
		}).forEach((tab) -> {
			Tab var10000 = (Tab) categoryMappings.put(tab.getStateObject(), tab);
		});
		Autumn.MANAGER_REGISTRY.moduleManager.getModules().stream().filter(Objects::nonNull)
				.sorted(Comparator.comparing(Module::getLabel)).map((module) -> {
					return Tabs.newTab(this.handler, module, (Tab) categoryMappings.get(module.getCategory()),
							Tabs.newTabBlock(),
							(TabBlock) ((Tab) categoryMappings.get(module.getCategory())).findChildren().get());
				}).forEach((tab) -> {
					((Tab) categoryMappings.get(((Module) tab.getStateObject()).getCategory()))
							.addChild(new Tab[] { tab });
				});
		categoryMappings.values().removeIf((category) -> {
			return ((TabBlock) ((Tab) category).findChildren().get()).sizeOf() == 0;
		});
		categoryMappings.values().stream().flatMap((category) -> {
			return StreamSupport.stream(((TabBlock) ((Tab) category).findChildren().get()).spliterator(), false);
		}).map(TabComponent::apply).filter((tab) -> {
			return !((Module) ((Tab) tab).getStateObject()).getOptions().isEmpty();
		}).forEach((tab) -> {
			((Module) ((Tab) tab).getStateObject()).getOptions().forEach((property) -> {
				((Tab) tab).addChild(
						new Tab[] { Tabs.newTab(this.handler, property, (TabBlock) ((Tab) tab).findChildren().get()) });
			});
		});
		this.handler.setCurrentTabs(categoryMappings.values());
	}

	public void draw(ScaledResolution sr) {
		if (!this.loaded) {
			this.setupTabs();
			this.loaded = true;
		}

		if (this.getParent().tabGui.getValue()) {
			this.handler.doTabRendering();
		}

	}
}
