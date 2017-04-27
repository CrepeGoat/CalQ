package awqatty.b.DrawMath.AlignDrawParts;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.SparseArray;

import java.util.List;

import awqatty.b.DrawMath.AssignParentheses.ClosureFlags;
import awqatty.b.DrawMath.DrawToCanvas.DrawForm;
import awqatty.b.ListTree.ListTree;

/**
 * Created by beckerawqatty on 11/21/16.
 */
/*
 * This class is built to host any of the DrawForm classes in an AlignForm tree
 */
public class AlignDraw implements AlignForm {

    private DrawForm comp;
    public AlignDraw(DrawForm draw) {comp = draw;}
    public DrawForm getDrawForm() {return comp;}

    @Override
    public void setSubLeafSizes(List<RectF> leaf_sizes) {}
    @Override
    public void getSubLeafLocations(SparseArray<RectF> leaf_locs) {}
    @Override
    public <T extends DrawAligned> void subBranchShouldUsePars(
            ListTree<T>.Navigator nav,
            boolean[] pars_active
    ) {}
    //@Override
    //public int getClosureFlags() {
    //    return comp.getClosureFlags();
    //}

    @Override
    public void getSize(RectF dst) {
        comp.getSize(dst);
    }

    @Override
    public void drawToCanvas(Canvas canvas, RectF dst) {
        comp.drawToCanvas(canvas, dst);
    }

    @Override
    public boolean intersectsTouchRegion(RectF dst, float px, float py) {
        return comp.intersectsTouchRegion(dst, px,py);
    }
    @Override
    public boolean intersectsTouchRegion(RectF dst, float p1_x, float p1_y, float p2_x, float p2_y) {
        return comp.intersectsTouchRegion(dst, p1_x,p1_y, p2_x,p2_y);
    }

    @Override
    public void setColor(int color) {
        comp.setColor(color);
    }

    @Override
    public void clearCache() {}
}
