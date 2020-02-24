import hudson.*
import hudson.security.*
import jenkins.*
import jenkins.model.*
import java.util.*
import java.lang.reflect.*
import java.util.logging.*
import groovy.json.*
import com.michelin.cio.hudson.plugins.rolestrategy.*

def env = System.getenv()


/**************/
/*   Roles    */
/**************/

def globalRoleRead = "Developer"
def globalBuildRole = "Deployer"
def globalRoleAdmin = "admin"
def globalRoleConfigure = "Prod-Deployer"

/*******************/
/* User and Groups */
/*******************/

def access = [
  admins: ["admin"],
  builders: ["admin"],
  readers: ["anonymous"]

]

/***************/
/* Permissions */
/***************/

def adminPermissions = [
"hudson.model.Hudson.Administer",
"hudson.model.Hudson.Read"

]

def DeveloperPermissions = [
"hudson.model.Hudson.Read",
"hudson.model.Item.Discover",
"hudson.model.Item.Read"

]

def DeployerPermissions = [
"hudson.model.Hudson.Read",
"hudson.model.Item.Build",
"hudson.model.Item.Cancel",
"hudson.model.Item.Read",
"hudson.model.Run.Replay"

]

def Prod-DeployerPermissions = [
"hudson.model.Hudson.Read",
"hudson.model.Item.Build",
"hudson.model.Item.Read",
"hudson.model.Run.Replay"

]


def roleBasedAuthenticationStrategy = new RoleBasedAuthorizationStrategy()
Jenkins.instance.setAuthorizationStrategy(roleBasedAuthenticationStrategy)

Constructor[] constrs = Role.class.getConstructors()
for (Constructor<?> c : constrs) {
  c.setAccessible(true)
}

// Make the method assignRole accessible
Method assignRoleMethod = RoleBasedAuthorizationStrategy.class.getDeclaredMethod("assignRole", String.class, Role.class, String.class)
assignRoleMethod.setAccessible(true)

Set<Permission> adminPermissionSet = new HashSet<Permission>()
adminPermissions.each { p ->
  def permission = Permission.fromId(p)
  if (permission != null) {
    adminPermissionSet.add(permission)
  } else {
    println("${p} is not a valid permission ID (ignoring)")
  }
}

Set<Permission> DeployerPermissionSet = new HashSet<Permission>()
DeployerPermissions.each { p ->
  def permission = Permission.fromId(p)
  if (permission != null) {
    DeployerPermissionSet.add(permission)
  } else {
    println("${p} is not a valid permission ID (ignoring)")
  }
}

Set<Permission> DeveloperPermissionSet = new HashSet<Permission>()
DeveloperPermissions.each { p ->
  def permission = Permission.fromId(p)
  if (permission != null) {
    DeveloperPermissionSet.add(permission)
  } else {
    println("${p} is not a valid permission ID (ignoring)")
  }
}

Set<Permission> Prod-DeployerPermissionSet = new HashSet<Permission>()
Prod-DeployerPermissions.each { p ->
  def permission = Permission.fromId(p)
  if (permission != null) {
    Prod-DeployerPermissionSet.add(permission)
  } else {
    println("${p} is not a valid permission ID (ignoring)")
  }
}

/************************/
/* Permissions -> Roles */
/************************/

// admins
Role adminRole = new Role(globalRoleAdmin, adminPermissionSet)
roleBasedAuthenticationStrategy.addRole(RoleBasedAuthorizationStrategy.GLOBAL, adminRole)

// Deployer
Role DeployerRole = new Role(globalRoleAdmin, DeployerPermissionSet)
roleBasedAuthenticationStrategy.addRole(RoleBasedAuthorizationStrategy.GLOBAL, DeployerrRole)

//  Developer
Role DeveloperRole = new Role(globalRoleRead, DeveloperPermissionSet)
roleBasedAuthenticationStrategy.addRole(RoleBasedAuthorizationStrategy.GLOBAL, DeveloperRole)

// Prod-Deployer
Role Prod-DeployerRole = new Role(globalRoleRead, readPermissionSet)
roleBasedAuthenticationStrategy.addRole(RoleBasedAuthorizationStrategy.GLOBAL, Prod-DeployerRole)

/************************/
/* Roles -> Group/Users */
/************************/

access.admins.each { l ->
  println("Granting admin to ${l}")
  roleBasedAuthenticationStrategy.assignRole(RoleBasedAuthorizationStrategy.GLOBAL, adminRole, l)
}

access.builders.each { l ->
  println("Granting Deployer to ${l}")
  roleBasedAuthenticationStrategy.assignRole(RoleBasedAuthorizationStrategy.GLOBAL, DeployerRole, l)
}

access.readers.each { l ->
  println("Granting Developer to ${l}")
  roleBasedAuthenticationStrategy.assignRole(RoleBasedAuthorizationStrategy.GLOBAL, DeveloperRole, l)
}

access.readers.each { l ->
  println("Granting Prod-Deployer to ${l}")
  roleBasedAuthenticationStrategy.assignRole(RoleBasedAuthorizationStrategy.GLOBAL, Prod-DeployerRole, l)
}
Jenkins.instance.save()
