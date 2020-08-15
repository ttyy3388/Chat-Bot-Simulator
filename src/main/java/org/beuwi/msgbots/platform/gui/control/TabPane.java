package org.beuwi.msgbots.platform.gui.control;

import com.sun.javafx.scene.control.TabObservableList;
import javafx.beans.DefaultProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

@DefaultProperty("tabs")
public class TabPane extends StackPane
{
	// public static final EventType<Event> SELECTION_CHANGED_EVENT = new EventType<Event> (Event.ANY, "SELECTION_CHANGED_EVENT");

	private static final String DEFAULT_STYLE_CLASS = "tab-pane";

	private static final double DEFAULT_MIN_WIDTH = 200;
	private static final double DEFAULT_MIN_HEIGHT = 500;

	private static final double DEFAULT_TAB_WIDTH = 100;
	private static final double DEFAULT_TAB_HEIGHT = 30;

	private static final double DEFAULT_SCROLL_SPEED = 0.005;

	// private static final double DEFAULT_TAB_WIDTH = Double.MAX_VALUE;
	// private static final double DEFAULT_TAB_HEIGHT = Double.MAX_VALUE;
	// private static final double DEFAULT_TAB_HEIGHT = Double.MAX_VALUE;

	private static final PseudoClass DEFAULT_TAB_PSEUDO_CLASS = PseudoClass.getPseudoClass("selected");

	// Tab Type Property
	private final ObjectProperty<Type> type = new SimpleObjectProperty<>(Type.NORMAL);

	// Tab Side Property
	private final ObjectProperty<Side> side = new SimpleObjectProperty<>(Side.TOP);

	// Selected Tab Property
	private final ObjectProperty<Tab> property = new SimpleObjectProperty();

	// Tab List
	private final ObservableList<Tab> tabs = new TabObservableList<>(new ArrayList<>());

	// Tab Content Pane
	private final StackPane content = new StackPane();

	// Tab Header Scroll Pane
	private ScrollPane scroll = new ScrollPane();

	// Tab Header Area (Default : HBox)
	private Pane header = new HBox();

	private Pane root = new VBox();

	public enum Type
	{
		NORMAL, SYSTEM
	};

	// Scroll Bar Pos
	private int pos = 0;

	{
		VBox.setVgrow(content, Priority.ALWAYS);
		HBox.setHgrow(content, Priority.ALWAYS);
	}

	public TabPane()
	{
		this(null);
	}

	public TabPane(Tab... tabs)
	{
		if (tabs != null)
		{
			getTabs().addAll(tabs);
		}

		this.tabs.addListener((ListChangeListener<Tab>) change ->
		{
			while (change.next())
			{
				for (Tab tab : change.getRemoved())
				{
					tab.setTabPane(null);

					header.getChildren().remove(tab);
				}

				for (Tab tab : change.getAddedSubList())
				{
					if (tab != null)
					{
						tab.setTabPane(this);

						header.getChildren().add(tab);

						property.set(tab);
					}
				}
			}
		});

		header.getStyleClass().add("tab-header-area");
		content.getStyleClass().add("tab-content-area");

		type.addListener((observable, oldType, newType) ->
		{
			switch (newType)
			{
				case NORMAL : break;
				case SYSTEM :

					if (getSide().isVertical())
					{
						return ;
					}

					List<Tab> list = this.tabs;

					widthProperty().addListener(change ->
					{
						for (Tab tab : list)
						{
							tab.setMinWidth(getWidth() / list.size());
						}
					});

					break;
			}
		});

		property.addListener((observable, oldTab, newTab) ->
		{
			if (oldTab != null)
			{
				content.getChildren().remove(oldTab.getContent());
				content.getChildren().add(newTab.getContent());

				oldTab.pseudoClassStateChanged(DEFAULT_TAB_PSEUDO_CLASS, false);
				newTab.pseudoClassStateChanged(DEFAULT_TAB_PSEUDO_CLASS, true);
			}
		});

		setSide(Side.TOP);
		setMinWidth(DEFAULT_MIN_WIDTH);
		setMinHeight(DEFAULT_MIN_HEIGHT);
		getStyleClass().setAll(DEFAULT_STYLE_CLASS);
	}

	public void select(int index)
	{
		setSelectedTab(getTab(index));
	}

	public void select(Tab tab)
	{
		setSelectedTab(tab);
	}

	public void addTab(Tab... tabs)
	{
		for (Tab tab : tabs)
		{
			if (!isContains(tab))
			{
				this.tabs.add(tab);
			}
		}
	}

	public boolean isContains(Tab tab)
	{
		return tabs.indexOf(tab) != -1;
	}

	public int getIndex(Tab tab)
	{
		return findTab(tab);
	}

	public int findTab(Tab tab)
	{
		return tabs.indexOf(tab);
	}

	public void close(Tab tab)
	{
		if (!tab.isClosable())
		{
			return ;
		}

		int index = findTab(tab),
			size = tabs.size();

		if (size > 1 && index != -1)
		{
			// If have a next tab
			if (size > index + 1)
			{
				select(index + 1);
			}
			// If have a previous tab
			else if (size > index)
			{
				select(index - 1);
			}
		}

		tabs.remove(tab);
	}

	public Tab getTab(int index)
	{
		return tabs.get(index);
	}

	public ObservableList<Tab> getTabs()
	{
		return tabs;
	}

	public void setSelectedTab(Tab tab)
	{
		property.set(tab);
	}

	public void moveSelectTab(int start, int delta)
	{
		if (getTabs().isEmpty())
		{
			return;
		}

		int index = start + delta;

		if (index != -1)
		{
			if (tabs.size() < index + 1)
			{
				select(0);
			}
			else
			{
				select(index);
			}
		}
		else
		{
			select(tabs.size() - 1);
		}
	}

	public void selectNextTab(Tab tab)
	{
		moveSelectTab(getIndex(tab), 1);
	}

	public void selectPreviousTab(Tab tab)
	{
		moveSelectTab(getIndex(tab),-1);
	}

	public Type getType()
	{
		return type.get();
	}

	public void setType(Type type)
	{
		this.type.set(type);
	}

	public Side getSide()
	{
		return side.get();
	}

	public void setSide(Side side)
	{
		scroll = new ScrollPane();

		switch (side)
		{
			case TOP    -> root = new VBox(scroll, content);
			case RIGHT  -> root = new HBox(content, scroll);
			case BOTTOM -> root = new VBox(content, scroll);
			case LEFT   -> root = new HBox(scroll, content);
		}

		// LEFT, RIGHT
		if (side.isVertical())
		{
			header = new VBox();

			scroll.setVvalue(1.0d);
			scroll.setMinWidth(DEFAULT_TAB_WIDTH);
			scroll.setFitToWidth(true);

			scroll.setOnScroll(event ->
			{
				scroll.setVvalue(scroll.getVvalue() - (event.getDeltaX()  * DEFAULT_SCROLL_SPEED));
			});
		}
		else
		{
			header = new HBox();

			scroll.setHvalue(1.0d);
			scroll.setMinHeight(DEFAULT_TAB_HEIGHT);
			scroll.setFitToHeight(true);

			scroll.setOnScroll(event ->
			{
				scroll.setHvalue(scroll.getHvalue() - (event.getDeltaY()  * DEFAULT_SCROLL_SPEED));
			});
		}

		header.getChildren().addAll(tabs);

		scroll.setContent(header);
		scroll.setHbarPolicy(ScrollBarPolicy.NEVER);
		scroll.setVbarPolicy(ScrollBarPolicy.NEVER);

		getChildren().setAll(root);

		this.side.set(side);
	}

	public Node getRoot()
	{
		return root;
	}

	public Node getHeaderArea()
	{
		return header;
	}

	public Node getContentArea()
	{
		return content;
	}

	public Tab getSelectedTab()
	{
		return property.get();
	}
}