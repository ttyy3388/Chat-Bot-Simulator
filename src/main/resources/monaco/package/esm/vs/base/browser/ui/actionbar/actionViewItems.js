/*---------------------------------------------------------------------------------------------
 *  Copyright (c) Microsoft Corporation. All rights reserved.
 *  Licensed under the MIT License. See License.txt in the project root for license information.
 *--------------------------------------------------------------------------------------------*/
import './actionbar.css';
import * as platform from '../../../common/platform.js';
import * as nls from '../../../../nls.js';
import { Disposable } from '../../../common/lifecycle.js';
import { Action, ActionRunner, Separator } from '../../../common/actions.js';
import * as DOM from '../../dom.js';
import * as types from '../../../common/types.js';
import { EventType, Gesture } from '../../touch.js';
import { DataTransfers } from '../../dnd.js';
import { isFirefox } from '../../browser.js';
export class BaseActionViewItem extends Disposable {
    constructor(context, action, options = {}) {
        super();
        this.options = options;
        this._context = context || this;
        this._action = action;
        if (action instanceof Action) {
            this._register(action.onDidChange(event => {
                if (!this.element) {
                    // we have not been rendered yet, so there
                    // is no point in updating the UI
                    return;
                }
                this.handleActionChangeEvent(event);
            }));
        }
    }
    handleActionChangeEvent(event) {
        if (event.enabled !== undefined) {
            this.updateEnabled();
        }
        if (event.checked !== undefined) {
            this.updateChecked();
        }
        if (event.class !== undefined) {
            this.updateClass();
        }
        if (event.label !== undefined) {
            this.updateLabel();
            this.updateTooltip();
        }
        if (event.tooltip !== undefined) {
            this.updateTooltip();
        }
    }
    get actionRunner() {
        if (!this._actionRunner) {
            this._actionRunner = this._register(new ActionRunner());
        }
        return this._actionRunner;
    }
    set actionRunner(actionRunner) {
        this._actionRunner = actionRunner;
    }
    getAction() {
        return this._action;
    }
    isEnabled() {
        return this._action.enabled;
    }
    setActionContext(newContext) {
        this._context = newContext;
    }
    render(container) {
        const element = this.element = container;
        this._register(Gesture.addTarget(container));
        const enableDragging = this.options && this.options.draggable;
        if (enableDragging) {
            container.draggable = true;
            if (isFirefox) {
                // Firefox: requires to set a text data transfer to get going
                this._register(DOM.addDisposableListener(container, DOM.EventType.DRAG_START, e => { var _a; return (_a = e.dataTransfer) === null || _a === void 0 ? void 0 : _a.setData(DataTransfers.TEXT, this._action.label); }));
            }
        }
        this._register(DOM.addDisposableListener(element, EventType.Tap, e => this.onClick(e)));
        this._register(DOM.addDisposableListener(element, DOM.EventType.MOUSE_DOWN, e => {
            if (!enableDragging) {
                DOM.EventHelper.stop(e, true); // do not run when dragging is on because that would disable it
            }
            if (this._action.enabled && e.button === 0) {
                DOM.addClass(element, 'active');
            }
        }));
        if (platform.isMacintosh) {
            // macOS: allow to trigger the button when holding Ctrl+key and pressing the
            // main mouse button. This is for scenarios where e.g. some interaction forces
            // the Ctrl+key to be pressed and hold but the user still wants to interact
            // with the actions (for example quick access in quick navigation mode).
            this._register(DOM.addDisposableListener(element, DOM.EventType.CONTEXT_MENU, e => {
                if (e.button === 0 && e.ctrlKey === true) {
                    this.onClick(e);
                }
            }));
        }
        this._register(DOM.addDisposableListener(element, DOM.EventType.CLICK, e => {
            DOM.EventHelper.stop(e, true);
            // menus do not use the click event
            if (!(this.options && this.options.isMenu)) {
                platform.setImmediate(() => this.onClick(e));
            }
        }));
        this._register(DOM.addDisposableListener(element, DOM.EventType.DBLCLICK, e => {
            DOM.EventHelper.stop(e, true);
        }));
        [DOM.EventType.MOUSE_UP, DOM.EventType.MOUSE_OUT].forEach(event => {
            this._register(DOM.addDisposableListener(element, event, e => {
                DOM.EventHelper.stop(e);
                DOM.removeClass(element, 'active');
            }));
        });
    }
    onClick(event) {
        var _a;
        DOM.EventHelper.stop(event, true);
        const context = types.isUndefinedOrNull(this._context) ? ((_a = this.options) === null || _a === void 0 ? void 0 : _a.useEventAsContext) ? event : undefined : this._context;
        this.actionRunner.run(this._action, context);
    }
    focus() {
        if (this.element) {
            this.element.focus();
            DOM.addClass(this.element, 'focused');
        }
    }
    blur() {
        if (this.element) {
            this.element.blur();
            DOM.removeClass(this.element, 'focused');
        }
    }
    updateEnabled() {
        // implement in subclass
    }
    updateLabel() {
        // implement in subclass
    }
    updateTooltip() {
        // implement in subclass
    }
    updateClass() {
        // implement in subclass
    }
    updateChecked() {
        // implement in subclass
    }
    dispose() {
        if (this.element) {
            DOM.removeNode(this.element);
            this.element = undefined;
        }
        super.dispose();
    }
}
export class ActionViewItem extends BaseActionViewItem {
    constructor(context, action, options = {}) {
        super(context, action, options);
        this.options = options;
        this.options.icon = options.icon !== undefined ? options.icon : false;
        this.options.label = options.label !== undefined ? options.label : true;
        this.cssClass = '';
    }
    render(container) {
        super.render(container);
        if (this.element) {
            this.label = DOM.append(this.element, DOM.$('a.action-label'));
        }
        if (this.label) {
            if (this._action.id === Separator.ID) {
                this.label.setAttribute('role', 'presentation'); // A separator is a presentation item
            }
            else {
                if (this.options.isMenu) {
                    this.label.setAttribute('role', 'menuitem');
                }
                else {
                    this.label.setAttribute('role', 'button');
                }
            }
        }
        if (this.options.label && this.options.keybinding && this.element) {
            DOM.append(this.element, DOM.$('span.keybinding')).textContent = this.options.keybinding;
        }
        this.updateClass();
        this.updateLabel();
        this.updateTooltip();
        this.updateEnabled();
        this.updateChecked();
    }
    focus() {
        super.focus();
        if (this.label) {
            this.label.focus();
        }
    }
    updateLabel() {
        if (this.options.label && this.label) {
            this.label.textContent = this.getAction().label;
        }
    }
    updateTooltip() {
        let title = null;
        if (this.getAction().tooltip) {
            title = this.getAction().tooltip;
        }
        else if (!this.options.label && this.getAction().label && this.options.icon) {
            title = this.getAction().label;
            if (this.options.keybinding) {
                title = nls.localize({ key: 'titleLabel', comment: ['action title', 'action keybinding'] }, "{0} ({1})", title, this.options.keybinding);
            }
        }
        if (title && this.label) {
            this.label.title = title;
        }
    }
    updateClass() {
        if (this.cssClass && this.label) {
            DOM.removeClasses(this.label, this.cssClass);
        }
        if (this.options.icon) {
            this.cssClass = this.getAction().class;
            if (this.label) {
                DOM.addClass(this.label, 'codicon');
                if (this.cssClass) {
                    DOM.addClasses(this.label, this.cssClass);
                }
            }
            this.updateEnabled();
        }
        else {
            if (this.label) {
                DOM.removeClass(this.label, 'codicon');
            }
        }
    }
    updateEnabled() {
        if (this.getAction().enabled) {
            if (this.label) {
                this.label.removeAttribute('aria-disabled');
                DOM.removeClass(this.label, 'disabled');
                this.label.tabIndex = 0;
            }
            if (this.element) {
                DOM.removeClass(this.element, 'disabled');
            }
        }
        else {
            if (this.label) {
                this.label.setAttribute('aria-disabled', 'true');
                DOM.addClass(this.label, 'disabled');
                DOM.removeTabIndexAndUpdateFocus(this.label);
            }
            if (this.element) {
                DOM.addClass(this.element, 'disabled');
            }
        }
    }
    updateChecked() {
        if (this.label) {
            if (this.getAction().checked) {
                DOM.addClass(this.label, 'checked');
            }
            else {
                DOM.removeClass(this.label, 'checked');
            }
        }
    }
}
