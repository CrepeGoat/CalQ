package awqatty.b.CustomEventListeners;

import android.content.Context;

import java.io.StringWriter;

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
			EVENT_GETTEXT		= 3,
			EVENT_SETTEXT		= 4,
			EVENT_SHUFFLE		= 5,
			EVENT_DELETE		= 6,
			EVENT_DELETEROOT	= 7;
	
	
	public ObservedOpTree(Context context) {
		super(context);
		listener = null;
	}
	public void setOnChangeListener(OnChangeListener on_change) {
		listener = on_change;
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
	public void addFunction(FunctionType ftype) {
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_ADDFUNCTION, PRE_EVENT) );
		//final boolean tmp =
		super.addFunction(ftype);
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_ADDFUNCTION, POST_EVENT) );
		//return tmp;
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
	public String getText() {
		String text;
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_GETTEXT, PRE_EVENT) );
		text = super.getText();
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_GETTEXT, POST_EVENT) );
		return text;
	}
	@Override
	public void setText(String str) {
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_SETTEXT, PRE_EVENT) );
		super.setText(str);
		if (listener != null) listener.onChange(
				new ChangeEvent(this, EVENT_SETTEXT, POST_EVENT) );
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
