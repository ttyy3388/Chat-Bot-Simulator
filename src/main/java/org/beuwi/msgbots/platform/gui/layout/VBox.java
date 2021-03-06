package org.beuwi.msgbots.platform.gui.layout;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import org.beuwi.msgbots.platform.gui.base.Layout;

public class VBox<T extends Node> extends javafx.scene.layout.VBox implements Layout {
    private static final String DEFAULT_STYLE_CLASS = "vbox";

    private final BooleanProperty fittableProperty = new SimpleBooleanProperty();

    // 순수 너비만 가져오도록 "Margin, Padding" 요소를 제거한 너비를 가져옴
    /* 왜냐하면 부모 너비(P)는 '100'인데 패딩 '20'때문에 자식 너비가 '80'이라면
     * (P)100, (C)80 -> (C)100 -> (P)120 (C)100 -> (C)120... 무한반복
     */
    private final InvalidationListener listener = new InvalidationListener() {
        @Override
        public void invalidated(Observable observable) {
            for (Node item : getChildren()) {
                if (item instanceof Region) {
                    double value = getWidth();

                    Insets margin = getMargin(VBox.this);
                    Insets padding = getPadding();
                    Insets border = null;
                    if (getBorder() != null) {
                        border = getBorder().getInsets();
                    }
                    else {
                        // 어차피 0을 빼주면 똑같으므로 해당 방법 사용
                        border = new Insets(0, 0, 0, 0);
                    }

                    // 테두리 제거 너비
                    if (border != null) {
                        value -= (border.getLeft() + border.getRight());
                    }
                    // 마진 제거 너비
                    if (margin != null) {
                        value -= (margin.getLeft() + margin.getRight());
                    }
                    // 패딩 제거 너비
                    if (padding != null) {
                        value -= (padding.getLeft() + padding.getRight());
                    }

                    ((Region) item).setPrefWidth(value);
                }
            }
        }
    };

    public VBox() {
        this(null);
    }

    public VBox(Node... items) {
        if (items != null) {
            getChildren().addAll(items);
        }

        fittableProperty().addListener(event-> {
            if (isFittable()) {
                widthProperty().addListener(listener);
            }
            else {
                widthProperty().removeListener(listener);
            }
        });

        setFittable(true); // Default
        // setOnMouseClicked();
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    public Node findById(String id) {
        return findById(this, id);
    };

    /* public int findItem(String id) {
        for (int index = 0 ; index < getItems().size() ; index ++) {
            if (getItems().get(index).getId().equals(id)) {
                return index;
            }
        }

        return -1;
    } */

    public void setFittable(boolean fittable) {
        this.fittableProperty.set(fittable);
    }

    public boolean isFittable() {
        return fittableProperty.get();
    }

    public BooleanProperty fittableProperty() {
        return fittableProperty;
    }

    /* @Override
    public ObservableList<T> getChildren() {
        return (ObservableList) super.getChildren();
    } */
}