package scripts.state.edge;

import java.awt.Point;

import org.powerbot.game.api.methods.input.Mouse;
import org.powerbot.game.api.methods.node.Menu;
import org.powerbot.game.api.methods.tab.Inventory;
import org.powerbot.game.api.methods.widget.Camera;
import org.powerbot.game.api.util.Filter;
import org.powerbot.game.api.wrappers.Entity;
import org.powerbot.game.api.wrappers.Locatable;
import org.powerbot.game.api.wrappers.node.Item;

import scripts.farming.EntityWrapper;
import scripts.state.Condition;
import scripts.state.Constant;
import scripts.state.State;
import scripts.state.Value;

public class UseItemWith<T extends Locatable & Entity> extends Task {

	Value<Integer> id;
	Value<T> object = null;
	Value<String> option = null;

	public UseItemWith(Condition c, State s, Integer id_,
			final Value<T> object_) {
		this(c, s, new Constant<Integer>(id_), object_);
	}


	public UseItemWith(Condition c, State s, Value<Integer> id_,
			final Value<T> object_) {
		super(null, s);
		id = id_;
		object = object_;
		setCondition(c.and(new Condition() {
			public boolean validate() {
				return Inventory.getItem(id.get()) != null;
			}
		}));
	}

	// e.g. to filter certain Item -> Object strings
	public UseItemWith<T> setOption(Value<String> option_) {
		option = option_;
		return this;
	}

	@Override
	public void run() {
		final Item item = Inventory.getItem(id.get());
		if (item != null)
			item.getWidgetChild().interact("Use");
		T sceneObject = object.get();
		if (sceneObject != null) {
			if(!sceneObject.isOnScreen()) {
				Camera.setPitch(99);
				Camera.turnTo(sceneObject);
			}
			while (Mouse.getLocation().distance(sceneObject.getCentralPoint()) > 1)
				Mouse.move(sceneObject.getCentralPoint());
			if (option == null) {
				Mouse.click(false);
				if (!Menu.select("Use")) {
					item.getWidgetChild().interact("Use");
					sceneObject.interact("Use");
				}
			} else {
				Mouse.apply(new EntityWrapper(sceneObject), new Filter<Point>() {
					public boolean accept(Point p) {
						return Menu.select("Use",item.getName() + " -> " + option.get());
					}
				});
				//System.out.println("Interaction: " +item.getName() + " -> " + option.get() );
				//System.out.println("ID: " + ((SceneObject) sceneObject).getId());
				//Mouse.move(sceneObject.getNextViewportPoint());
				/*for(String optionx : Menu.getOptions()) {
					System.out.println(optionx);
					System.out.println("optionx: " + optionx.equals(item.getName() + " -> " + option.get()));
				}
				Mouse.move(sceneObject.getCentralPoint());
				for(String optionx : Menu.getOptions()) {
					System.out.println(optionx);
				}
				sceneObject.interact("Use", item.getName() + " -> " + option.get());*/
				//Menu.select("Use",item.getName() + " -> " + option.get());
				/*for(String option : Menu.getOptions()) {
					if(filter.accept(option)) {
						sceneObject.interact("Use", option);
					}
				}*/
			}
		}
	}
}
