package awqatty.b.CustomEventListeners;

import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.OpTree.OpTree;
import awqatty.b.TextPresentation.TextPresBuilderForm;

public class ObservedOpTree extends OpTree {

	private OnChangeListener listener;
	public static final byte
			PRE_EVENT = 0,
			POST_EVENT = 1,
			EVENT_ANY = 0,
			EVENT_TEXTPRES = 1,
			EVENT_SELECTION = 2,
			EVENT_ADDFUNCTION = 3,
			EVENT_ADDNUMBER = 4,
			EVENT_SHUFFLE = 5,
			EVENT_DELETE = 6,
			EVENT_DELETEPARENT = 7;
	
	
	public ObservedOpTree(TextPresBuilderForm tpb) {
		super(tpb);
		listener = null;
	}
	public void setOnChangeListener(OnChangeListener on_change) {
		listener = on_change;
	}
	
	@Override
	public void setTextPresBuilder(TextPresBuilderForm tpb) {
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_TEXTPRES, PRE_EVENT) );
		super.setTextPresBuilder(tpb);
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_TEXTPRES, POST_EVENT) );
	}
	
	@Override
	public void setSelection(int i) {
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_SELECTION, PRE_EVENT) );
		super.setSelection(i);
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_SELECTION, POST_EVENT) );
	}
	
	@Override
	public boolean addFunction(FunctionType ftype) {
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_ADDFUNCTION, PRE_EVENT) );
		final boolean tmp = super.addFunction(ftype);
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_ADDFUNCTION, POST_EVENT) );
		return tmp;
	}
	
	@Override
	public void addNumber (double num) {
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_ADDNUMBER, PRE_EVENT) );
		super.addNumber(num);
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_ADDNUMBER, POST_EVENT) );
	}
	
	@Override
	public void shuffleOrder() {
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_SHUFFLE, PRE_EVENT) );
		super.shuffleOrder();
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_SHUFFLE, POST_EVENT) );
	}
	
	@Override
	public void delete() {
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_DELETE, PRE_EVENT) );
		super.delete();
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_DELETE, POST_EVENT) );
	}
	
	@Override
	public void deleteParent() {
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_DELETEPARENT, PRE_EVENT) );
		super.deleteParent();
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_DELETEPARENT, POST_EVENT) );
	}
}
