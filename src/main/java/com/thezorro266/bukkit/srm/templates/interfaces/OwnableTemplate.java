/**
 * SimpleRegionMarket
 * Copyright (C) 2013  theZorro266 <http://www.thezorro266.com>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.thezorro266.bukkit.srm.templates.interfaces;

import org.bukkit.OfflinePlayer;

import com.thezorro266.bukkit.srm.helpers.Region;

public interface OwnableTemplate {

	public boolean isRegionOwner(OfflinePlayer player, Region region);

	public boolean isRegionMember(OfflinePlayer player, Region region);

	public boolean isRegionOccupied(Region region);

	public OfflinePlayer[] getRegionOwners(Region region);

	public OfflinePlayer[] getRegionMembers(Region region);
}