[1mdiff --git a/src/main/java/com/promcteam/fabled/api/target/TargetHelper.java b/src/main/java/com/promcteam/fabled/api/target/TargetHelper.java[m
[1mindex 7b95ae224..715d4a602 100644[m
[1m--- a/src/main/java/com/promcteam/fabled/api/target/TargetHelper.java[m
[1m+++ b/src/main/java/com/promcteam/fabled/api/target/TargetHelper.java[m
[36m@@ -72,7 +72,7 @@[m [mpublic abstract class TargetHelper {[m
      *[m
      * @param source living entity to get the target of[m
      * @param range  maximum range to check[m
[31m-     * @return entity player is looing at or null if not found[m
[32m+[m[32m     * @return entity player is looking at or null if not found[m
      */[m
     public static LivingEntity getLivingTarget(LivingEntity source, double range) {[m
         return getLivingTarget(source, range, 4);[m
[36m@@ -89,7 +89,7 @@[m [mpublic abstract class TargetHelper {[m
      */[m
     public static LivingEntity getLivingTarget(LivingEntity source, double range, double tolerance) {[m
         List<LivingEntity> targets = getLivingTargets(source, range, tolerance);[m
[31m-        if (targets.size() == 0) return null;[m
[32m+[m[32m        if (targets.isEmpty()) return null;[m
         return targets.get(0);[m
     }[m
 [m
[36m@@ -101,7 +101,8 @@[m [mpublic abstract class TargetHelper {[m
      * @param range  range of the cone[m
      * @return list of targets[m
      */[m
[31m-    public static List<LivingEntity> getConeTargets(LivingEntity source, double arc, double range) {[m
[32m+[m[32m//     * @param resetY whether to reset the Y value of the target[m
[32m+[m[32m    public static List<LivingEntity> getConeTargets(LivingEntity source, double arc, double range, boolean resetY) {[m
         List<LivingEntity> targets = new ArrayList<>();[m
         List<Entity>       list    = source.getNearbyEntities(range, range, range);[m
         if (arc <= 0) return targets;[m
[36m@@ -109,7 +110,7 @@[m [mpublic abstract class TargetHelper {[m
         // Initialize values[m
         Location sourceLocation = source.getEyeLocation();[m
         Vector   dir            = sourceLocation.getDirection();[m
[31m-        dir.setY(0);[m
[32m+[m[32m        if(resetY) dir.setY(0);[m
         double cos   = Math.cos(arc * Math.PI / 180);[m
         double cosSq = cos * cos;[m
 [m
[36m@@ -129,7 +130,7 @@[m [mpublic abstract class TargetHelper {[m
                             .add(0, getHeight(entity) * 0.5, 0)[m
                             .subtract(sourceLocation)[m
                             .toVector();[m
[31m-                    relative.setY(0);[m
[32m+[m[32m                    if(resetY) relative.setY(0);[m
                     double dot   = relative.dot(dir);[m
                     double value = dot * dot / relative.lengthSquared();[m
                     if (arc < 180 && dot > 0 && value >= cosSq) targets.add((LivingEntity) entity);[m
[1mdiff --git a/src/main/java/com/promcteam/fabled/dynamic/target/ConeTarget.java b/src/main/java/com/promcteam/fabled/dynamic/target/ConeTarget.java[m
[1mindex ae6a1c471..a21f90bf8 100644[m
[1m--- a/src/main/java/com/promcteam/fabled/dynamic/target/ConeTarget.java[m
[1m+++ b/src/main/java/com/promcteam/fabled/dynamic/target/ConeTarget.java[m
[36m@@ -44,8 +44,9 @@[m [mimport java.util.function.Supplier;[m
  * each of the current targets.[m
  */[m
 public class ConeTarget extends TargetComponent {[m
[31m-    private static final String ANGLE = "angle";[m
[31m-    private static final String RANGE = "range";[m
[32m+[m[32m    private static final String ANGLE   = "angle";[m
[32m+[m[32m    private static final String RANGE   = "range";[m
[32m+[m[32m    private static final String RESET_Y = "reset-y";[m
 [m
 [m
     /**[m
[36m@@ -157,7 +158,10 @@[m [mpublic class ConeTarget extends TargetComponent {[m
     List<LivingEntity> getTargets(LivingEntity caster, int level, List<LivingEntity> targets) {[m
         double range = parseValues(caster, RANGE, level, 3.0);[m
         double angle = parseValues(caster, ANGLE, level, 90.0);[m
[31m-        return determineTargets(caster, level, targets, t -> TargetHelper.getConeTargets(t, angle, range));[m
[32m+[m[32m        return determineTargets(caster,[m
[32m+[m[32m                level,[m
[32m+[m[32m                targets,[m
[32m+[m[32m                t -> TargetHelper.getConeTargets(t, angle, range, settings.getBool(RESET_Y, true)));[m
     }[m
 [m
     @Override[m
