import { MaintenanceRequest } from "./MaintenanceRequest";
import { RoomDetailsRequest } from "./RoomDetailsRequest";

export class MaintenanceFullDetailsRequest {
    roomDetails!: RoomDetailsRequest;
    maintenances!: MaintenanceRequest;
  }