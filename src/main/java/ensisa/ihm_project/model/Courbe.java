package ensisa.ihm_project.model;

import java.util.ArrayList;
import java.util.List;


public class Courbe {
    private int n;
    private List<Point> points;

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
            points.add(new Point(val,0));
        }
    }

    //Poynome de Lagrange
    public double lagrange(double x, int i) {
        double produit = 1;
        double xi = points.get(i).getX();
        for (int j = 0; j < n-1; j++) {
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
        for (int i = 0; i < n -1; i++) {
            double yi = points.get(i).getY();
            Px += yi * lagrange(x, i);
        }
        //troncature
        if (Px > 255) return 255;
        if (Px < 0) return 0;
        return Px;
    }

}
