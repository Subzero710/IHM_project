package ensisa.ihm_project.model;

import java.util.ArrayList;
import java.util.List;


public class Courbe {
    private int n;
    private List<Point> points;
/*
    public Courbe() {
        this.n = 4;
        points = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            double val = 255.0 * i / (n - 1);
            points.add(new Point(val,0));
        }
    }

    public Courbe(int n) {
        if (n<4) n=4;
        if (n>8) n=8;
        else this.n = n;
        for (int i = 0; i < n; i++) {
            double val = 255.0 * i / (n - 1);
            points.add(new Point(val,val));
        }
    }
*/

    public Courbe(int n) {
        if (n < 4) n = 4;
        if (n > 8) n = 8;
        this.n = n;

        points = new ArrayList<>();
        lineariser();
    }

    //Poynome de Lagrange
    public double lagrange(double x, int i) {
        double produit = 1;
        double xi = points.get(i).getX();
        for (int j = 0; j < n; j++) {
            if (i != j) {
                double xj = points.get(j).getX();
                produit *= (x - xj) / (xi - xj);
            }
        }
        return produit;
    }

    // Calcule polynome en x
    public double polynome(double x) {
        double Px = 0;
        for (int i = 0; i < n ; i++) {
            double yi = points.get(i).getY();
            Px += yi * lagrange(x, i);
        }
        //troncature
        if (Px > 255) return 255;
        if (Px < 0) return 0;
        return Px;
    }

    public void lineariser() {
        points.clear();
        for (int i = 0; i < n; i++) {
            double val = 255.0 * i / (n - 1);
            points.add(new Point(val, val));
        }
    }

    public void setPointY(int i, double y) {
        if (0 <= i && i < n) {
            if (y < 0) y = 0;
            if (y > 255) y = 255;
            points.get(i).setY(y);
        }
    }

    public int getN() {
        return n;
    }

    public double[] snapshotY() {
        double[] ys = new double[n];
        for (int i = 0; i < n; i++) {
            ys[i] = points.get(i).getY();
        }
        return ys;
    }

    public void restoreY(double[] ys) {
        if (ys == null) {
            return;
        }
        int len = Math.min(ys.length, n);
        for (int i = 0; i < len; i++) {
            setPointY(i, ys[i]);
        }
    }

    public List<Point> getPoints() {
        return points;
    }
}