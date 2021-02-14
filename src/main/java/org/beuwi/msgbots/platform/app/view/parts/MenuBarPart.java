package org.beuwi.msgbots.platform.app.view.parts;

import javafx.collections.ObservableMap;

import org.beuwi.msgbots.openapi.FormLoader;
import org.beuwi.msgbots.platform.app.action.OpenBrowserAction;
import org.beuwi.msgbots.platform.app.impl.View;
import org.beuwi.msgbots.platform.app.view.actions.OpenDialogBoxAction;
import org.beuwi.msgbots.platform.app.view.actions.OpenDocumentTabAction;
import org.beuwi.msgbots.platform.app.view.actions.OpenProgramTabAction;
import org.beuwi.msgbots.platform.app.view.actions.RefreshBotListAction;
import org.beuwi.msgbots.platform.app.view.dialogs.CreateBotDialog;
import org.beuwi.msgbots.platform.app.view.dialogs.ImportBotDialog;
import org.beuwi.msgbots.platform.app.view.tabs.GlobalConfigTab;
import org.beuwi.msgbots.platform.gui.control.MenuButton;
import org.beuwi.msgbots.platform.gui.control.MenuItem;
import org.beuwi.msgbots.platform.gui.control.MenuBar;
import org.beuwi.msgbots.platform.gui.control.Separator;
import org.beuwi.msgbots.platform.gui.layout.StackPane;
import org.beuwi.msgbots.platform.util.SharedValues;

public class MenuBarPart implements View {
	private static ObservableMap<String, Object> namespace;
	private static FormLoader loader;
	private static StackPane root;
	private static MenuBar component;

	@Override
	public void init() {
		loader = new FormLoader("part", "menu-bar-part");
		namespace = loader.getNamespace();
		root = loader.getRoot();
		component = loader.getComponent();

		MenuButton btnFileMenu = (MenuButton) namespace.get("btnFileMenu");
		MenuButton btnEditMenu = (MenuButton) namespace.get("btnEditMenu");
		MenuButton btnViewMenu = (MenuButton) namespace.get("btnViewMenu");
		MenuButton btnDebugMenu = (MenuButton) namespace.get("btnDebugMenu");
		MenuButton btnHelpMenu = (MenuButton) namespace.get("btnHelpMenu");

		// File Menu Button
		btnFileMenu.setMenus(
			new MenuItem("New Bot", "Ctrl + N", event -> {
				OpenDialogBoxAction.execute(new CreateBotDialog());
			}),
			new MenuItem("Import Script", "Ctrl + I", event -> {
				OpenDialogBoxAction.execute(new ImportBotDialog());
			}),
			new Separator(),
			new MenuItem("Save", "Ctrl + S"),
			new MenuItem("Save All"),
			new Separator(),
			new MenuItem("Refresh All Bots", "Ctrl + Alt + Y", event -> {
				RefreshBotListAction.execute();
			}),
			new Separator(),
			new MenuItem("Settings", "Ctrl + Alt + S", event -> {
				OpenProgramTabAction.execute(GlobalConfigTab.getRoot());
			})
		);

		// Edit Menu Button
		btnEditMenu.setMenus(
			new MenuItem("Undo", "Ctrl + Z").disable(true),
			new MenuItem("Redo", "Ctrl + Y").disable(true),
			new Separator(),
			new MenuItem("Cut", "Ctrl + X").disable(true),
			new MenuItem("Copy", "Ctrl + C").disable(true),
			new MenuItem("Paste", "Ctrl + V").disable(true)
		);

		// View Menu Button
		btnViewMenu.setMenus(
			new MenuItem("Toggle Menu Bar", "Alt + M").disable(true),
			new MenuItem("Toggle Side Bar", "Alt + S").disable(true),
			new MenuItem("Toggle Debug Area", "Alt + D").disable(true)
		);

		// Debug Menu Button
		btnDebugMenu.setMenus(
			new MenuItem("Compile"),
			new MenuItem("Power On / Off"),
			new Separator(),
			new MenuItem("Show Global Log", "F8"),
			new MenuItem("Open Debug Room", "F9")
		);

		// Help Menu Button
		btnHelpMenu.setMenus(
			new MenuItem("View License", event -> {
				OpenBrowserAction.execute(SharedValues.VIEW_LICENSE_LINK);
			}),
			new Separator(),
			new MenuItem("Release Notes", event -> {
				OpenBrowserAction.execute(SharedValues.RELEASE_NOTES_LINK);
			}),
			new Separator(),
			new MenuItem("About Program", event -> {
				OpenDocumentTabAction.execute(SharedValues.ABOUT_PROGRAM_DOCUMENT);
			}),
			new Separator(),
			new MenuItem("Welcome Guide", event -> {
				OpenDocumentTabAction.execute(SharedValues.WELCOME_GUIDE_DOCUMENT);
			})
		);
	}

	public static StackPane getRoot() {
		return root;
	}

	public static MenuBar getComponent() {
		return component;
	}

	public static <T> T getComponent(String key) {
		return (T) namespace.get(key);
	}

	public static ObservableMap<String, Object> getNamespace() {
		return namespace;
	}
}