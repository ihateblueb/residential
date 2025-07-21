# Residential

Modern nation and towns system for Paper, work in progress.

Java 21+, Paper 1.20+

## Plan

Residential intends to be a complete replacement for TownyAdvanced.
There will be both towns and nations. Both will have individual role systems allowing for different administrative actions (e.g. towns have a default "Land Manager" role that can use /t claim and /t unclaim).

### Inbox

Residents will have access to `/inbox`, a place where both towns and nations can send messages to communicate with their residents. Residential may also send messages to resident inboxes.

### Clock

Residential has a day system like Towny's. Every 5 minutes, the clock ticks. After 24 hours worth of ticks, a new day occurs and the clock is reset.

### Towns

Towns will be chunk based.

#### Land Protection

Mob removal: On spawn event, timer for removing ones wandering in
Fire
Explosion
PVP

### Nations

Nations will require at least one member town and require a capitol. The capitol's mayor does not have to be the nation's leader, but they can be.
Nations may distribute funds to towns by a flat amount or based on residents. 
Nations may distribute funds to residents by a flat amount.

#### Elections (maybe)

Nations may opt into an election system.
Nations may set a role requirement to enroll as a candidate.
Nations may set a role requirement to vote.
Nations may set an election cycle in number of Residential days.
Elections may be called by the leader.
Nations may opt into letting residents call for an election. If 25% of residents call for an election, one will occur.
Nations may set how long their elections take, by default they last 7 days.
At least 25% of the nation's population must vote for the leader elect to become leader.
Voting will be ranked choice.

### Tax

Both towns and nations will have tax systems. The server can tax towns, nations, and residents. Nations can tax towns. Towns can tax residents.
The server, nations, and towns, can allow taxes to be overdue at an additional cost with set timeframes before a taxpayer is deleted or abandoned (town), or kicked from the nation or town.
Towns may tax based on number of plots owned.
Tax can be balance percentage based.
The server tax on towns can be based on additional bought chunks or be a flat fee or percentage.

### Warps

Both nations and towns will have a warp system only available to residents.

### War

There is no plan for war.

## Status

Complete:
- Creating towns
- Teleporting to towns
- Claiming chunks for a town
- Withdrawing and depositing to town bank
- Changing town spawn
- Clock system

In Progress:
- Tax system
- Debt system

Todo:
- Changing town mayor
- Abandonment and deletion
- Toggles for various town properties
- Town protections (PVP, mobs, fire, then the town_permission type)
- Nations
- way more
