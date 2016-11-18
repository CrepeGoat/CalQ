package awqatty.b.CustomEventListeners;

import android.content.Context;
import awqatty.b.FunctionDictionary.FunctionType;
import awqatty.b.OpTree.OpTree;

public class ObservedOpTree extends OpTree {

	private OnChangeListener listener;
	public static final byte
			PRE_EVENT			= 0,
			POST_EVENT			= 1;

	public static final byte
			EVENT_SELECTION		= 0,
			EVENT_ADDFUNCTION	= 1,
			EVENT_ADDNUMBER		= 2,
			EVENT_SHUFFLE		= 3,
			EVENT_DELETE		= 4,
			EVENT_DELETEROOT	= 5;
	
	
	public ObservedOpTree(Context context) {
		super(context);
		listener = null;
	}
	public void setOnChangeListener(OnChangeListener on_change) {
		listener = on_change;
	}
	
	// TODO give select functions unique identifiers
	@Override
	public void resetSelection(int... i) {
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_SELECTION, PRE_EVENT) );
		super.resetSelection(i);
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_SELECTION, POST_EVENT) );
	}
	@Override
	public void addToSelection(int... i) {
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_SELECTION, PRE_EVENT) );
		super.addToSelection(i);
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_SELECTION, POST_EVENT) );
	}
	@Override
	public void finalizeSelection() {
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_SELECTION, PRE_EVENT) );
		super.finalizeSelection();
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_SELECTION, POST_EVENT) );
	}
	@Override
	public void selectNone() {
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_SELECTION, PRE_EVENT) );
		super.selectNone();
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
				new ChangeEvent(this, EVENT_DELETEROOT, PRE_EVENT) );
		super.deleteParent();
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_DELETEROOT, POST_EVENT) );
	}
}
