package studio.magemonkey.fabled.api.particle;

import org.bukkit.Location;
import org.bukkit.util.Vector;
import studio.magemonkey.fabled.api.Settings;
import studio.magemonkey.fabled.api.enums.Direction;
import studio.magemonkey.fabled.data.Matrix3D;
import studio.magemonkey.fabled.data.Point3D;
import studio.magemonkey.fabled.data.formula.Formula;
import studio.magemonkey.fabled.data.formula.value.CustomValue;

public class TimeBasedTransform {
    private static final Vector        UP     = new Vector(0, 1, 0);
    private static final CustomValue[] values = {
            new CustomValue("t"), // Represents time (iterations)
            new CustomValue("l"), // Represents the skill level
//            new CustomValue("s"),  // Represents the current effect scale
//            new CustomValue("r"),  // Represents the current effect rotation
//            new CustomValue("p"),  // Represents the current effect spin
//            new CustomValue("i"),  // Represents the current effect tilt
//            new CustomValue("x"),  // Represents the current effect x position
//            new CustomValue("y"),  // Represents the current effect y position
//            new CustomValue("z")   // Represents the current effect z position
    };
    private final        Formula       rotateFormula;
    private final        Formula       spinFormula;
    private final        Formula       tiltFormula;
    private final        Formula       scaleFormula;
    private final        Formula       translateFowardFormula;
    private final        Formula       translateUpFormula;
    private final        Formula       translateRightFormula;
    private final        Direction     direction;

    public TimeBasedTransform(Settings settings) {
        this.rotateFormula = new Formula(
                settings.getString("rotate", "0"),
                values
        );
        this.spinFormula = new Formula(
                settings.getString("spin", "0"),
                values
        );
        this.tiltFormula = new Formula(
                settings.getString("tilt", "0"),
                values
        );
        this.scaleFormula = new Formula(
                settings.getString("scale", "1"),
                values
        );
        this.translateFowardFormula = new Formula(
                settings.getString("forward", "0"),
                values
        );
        this.translateUpFormula = new Formula(
                settings.getString("upward", "0"),
                values
        );
        this.translateRightFormula = new Formula(
                settings.getString("right", "0"),
                values
        );

        this.direction = Direction.valueOf(settings.getString("direction", "XY"));
    }

    public Point3D[] apply(Point3D[] points, Location loc, boolean withRotation, int iteration, int level) {
        double rotate           = rotateFormula.compute(iteration, level);
        double spin             = spinFormula.compute(iteration, level);
        double tilt             = tiltFormula.compute(iteration, level);
        double newScale         = scaleFormula.compute(iteration, level);
        double translateForward = translateFowardFormula.compute(iteration, level);
        double translateUp      = translateUpFormula.compute(iteration, level);
        double translateRight   = translateRightFormula.compute(iteration, level);

        Matrix3D directionMatrix = direction.getMatrix();
        Matrix3D pointedRotation = withRotation ? MatrixUtil.getRotationMatrix(0, -loc.getYaw(), 0) : null;
        Matrix3D rotationMatrix  = MatrixUtil.getRotationMatrix(tilt, spin, rotate);
        Matrix3D scaleMatrix     = MatrixUtil.getScaleMatrix(newScale);

        Matrix3D finalMatrix = MatrixUtil.multiply(pointedRotation, directionMatrix, rotationMatrix, scaleMatrix);

        Vector dir   = loc.getDirection().setY(0).normalize();
        Vector right = dir.clone().crossProduct(UP).normalize();
        Vector offset = new Vector()
                .add(dir.clone().multiply(translateForward))
                .add(right.clone().multiply(translateRight))
                .setY(translateUp);
        return MatrixUtil.translate(finalMatrix.multiply(points), offset.getX(), offset.getY(), offset.getZ());
    }
}
