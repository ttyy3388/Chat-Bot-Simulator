package org.beuwi.simulator.platform.application.views.parts;

import com.jfoenix.controls.JFXToggleButton;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.beuwi.simulator.managers.FileManager;
import org.beuwi.simulator.platform.application.actions.*;
import org.beuwi.simulator.platform.application.views.dialogs.CreateBotDialog;
import org.beuwi.simulator.platform.ui.components.IContextMenu;
import org.beuwi.simulator.platform.ui.components.IMenuItem;
import org.beuwi.simulator.settings.Settings;

import java.util.HashMap;

public class ActiveAreaPart
{
	private static ObservableMap<String, Object> nameSpace;

	private static AnchorPane root;
	private static StackPane pane;

	public static void initialize() throws Exception
	{
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(ActiveAreaPart.class.getResource("/forms/ActiveAreaPart.fxml"));
		loader.setController(null);
		loader.load();

		nameSpace = loader.getNamespace();

		root = loader.getRoot();

		pane = (StackPane) nameSpace.get("stpResizeBar");

		pane.setOnMouseDragged(event ->
		{
			ResizeSideBarAction.update(event);
		});

		ExplorerTabPart.initialize();
		DebugTabPart.initialize();
	}

	private static class ExplorerTabPart
	{
		public static void initialize()
		{
			ToggleButton tab = (ToggleButton) nameSpace.get("tgnExplorerTab");
			tab.setOnMousePressed(event ->
			{
				if (event.isPrimaryButtonDown())
				{
					SelectActivityTabAction.update(0);
				}
			});

			// Option Button
			IContextMenu option = new IContextMenu
			(
				new IMenuItem("Show Compiled Check"),
				new IMenuItem("Show Compile Button"),
				new IMenuItem("Show Power Switch")
			);

			Button button = (Button) nameSpace.get("btnShowOption");
			button.setOnMousePressed(event ->
			{
				if (event.isPrimaryButtonDown())
				{
					option.show(button, event);
				}
			});

			// List View
			IContextMenu menu = new IContextMenu
			(
				new IMenuItem("New Bot", "Ctrl + N", event -> CreateBotDialog.display()),
				new SeparatorMenuItem(),
				new IMenuItem("Show in Explorer", "Shift + Alt + R", event -> ShowInExplorerAction.update(FileManager.BOTS_FOLDER)),
				new SeparatorMenuItem(),
				new IMenuItem("Copy Path", "Ctrl + Alt + C", event -> CopyAction.update(FileManager.BOTS_FOLDER.getAbsolutePath())),
				new IMenuItem("Copy Relative Path", "Ctrl + Shift + C", event -> CopyAction.update(FileManager.BOTS_FOLDER.getPath()))
			);

			ListView listView = (ListView) nameSpace.get("lsvExplorerArea");
			listView.setOnMousePressed(event ->
			{
				String target = event.getTarget().toString();

				if (event.isSecondaryButtonDown())
				{
					if (target.contains("ListViewSkin$"))
					{
						menu.show(listView, event);
					}
					else
					{
						menu.hide();
					}
				}
				else
				{
					menu.hide();
				}
			});
		}
	}

	private static class DebugTabPart
	{
		public static void initialize()
		{
			ToggleButton tab = (ToggleButton) nameSpace.get("tgnDebugTab");
			tab.setOnMousePressed(event ->
			{
				if (event.isPrimaryButtonDown())
				{
					SelectActivityTabAction.update(1);
				}
			});

			initButtonBar();
			initOptionPane();
		}

		private static void initButtonBar()
		{
			Button btnOpenChatRoom  = (Button) nameSpace.get("btnOpenChatRoom");
			Button btnShowGlobalLog = (Button) nameSpace.get("btnShowGlobalLog");
			Button btnReloadAllBots = (Button) nameSpace.get("btnReloadAllBots");

			btnOpenChatRoom.setOnAction(event ->
			{
				OpenDebugRoomTabAction.update();
			});

			btnShowGlobalLog.setOnAction(event ->
			{
				OpenGlobalLogTabAction.update();
			});

			btnReloadAllBots.setOnAction(event ->
			{
				// ReloadAllBotsAction.update();
			});
		}

		private static void initOptionPane()
		{
			// 추후 Chat Room이 켜져있어야지만 활성화 되도록 변경

			TextField txfRoomName    = (TextField) nameSpace.get("txfRoomName");
			TextField txfSenderName  = (TextField) nameSpace.get("txfSenderName");
			TextField txfBotName     = (TextField) nameSpace.get("txfBotName");
			TextField txfPackageName = (TextField) nameSpace.get("txfPackageName");

			JFXToggleButton tgnIsGroupChat   = (JFXToggleButton) nameSpace.get("tgnIsGroupChat");
			JFXToggleButton tgnSenderProfile = (JFXToggleButton) nameSpace.get("tgnSenderProfile");
			JFXToggleButton tgnBotProfile 	 = (JFXToggleButton) nameSpace.get("tgnBotProfile");

			Button btnSenderProfile = (Button) nameSpace.get("btnSenderProfile");
			Button btnBotProfile    = (Button) nameSpace.get("btnBotProfile");

			Button btnApply  = (Button) nameSpace.get("btnApply");
			Button btnCancel = (Button) nameSpace.get("btnCancel");

			Settings.Public data = Settings.getPublicSetting("chat");

			txfRoomName.setText(data.getString("room"));
			txfSenderName.setText(data.getString("sender"));
			txfBotName.setText(data.getString("bot"));
			txfPackageName.setText(data.getString("package"));

			tgnIsGroupChat.setSelected(data.getBoolean("isGroupChat"));
			tgnSenderProfile.setSelected(data.getBoolean("visibleSenderProfile"));
			tgnBotProfile.setSelected(data.getBoolean("visibleBotProfile"));

			btnSenderProfile.setOnAction(event -> {});
			btnBotProfile.setOnAction(event -> {});

			btnApply.setOnAction(event ->
			{
				HashMap<String, Object> map = new HashMap<>();

				map.put("room", txfRoomName.getText());
				map.put("sender", txfSenderName.getText());
				map.put("bot", txfBotName.getText());
				map.put("package", txfPackageName.getText());
				map.put("isGroupChat", tgnIsGroupChat.isSelected());
				map.put("visibleSenderProfile", tgnSenderProfile.isSelected());
				map.put("visibleBotProfile", tgnBotProfile.isSelected());

				data.putMap(map);
			});

			btnCancel.setOnAction(event ->
			{

			});
		}
	}

	public static AnchorPane getRoot()
	{
		return root;
	}

	// Children get(0) : Component
	public static HBox getComponent()
	{
		return (HBox) root.getChildren().get(0);
	}

	public static VBox getActivityBar()
	{
		return (VBox) getComponent().getChildren().get(0);
	}

	public static AnchorPane getSideBar()
	{
		return (AnchorPane) getComponent().getChildren().get(1);
	}

	public static ObservableMap<String, Object> getNameSpace()
	{
		return nameSpace;
	}
}