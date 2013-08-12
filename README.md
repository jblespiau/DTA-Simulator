DTA-Simulator
=============


Simulator of the Dynamic Traffic Assignment for the case of N parallel roads
composed of multiple cells

This implementation computes a User Equilibrium for Dynamic Traffic Assignement 
(also called Nash Equilibrium or Wardrop equilibrium) for the case of N parallel
links.

To run it, you just need to add the lib/*.jar into the build path.

Don't forget to enable assertions (-ea parameter on the JVM), it is used to
detect anormal behaviors.

Note
----

This development is not maintained anymore and was only a proof of concept
for the UE-DTA problem.
See https://github.com/Gueust/DTA-PC for a more developped project used to to
solve the System Optimal Dynamic Traffic Assignement with Partial Compliance
(SO-DTA-PC) problem.
In particular it contains a high level description of networks, a better
abstraction of the demand etc. However, this UE solver for parallel networks
is not supported yet by this version.
