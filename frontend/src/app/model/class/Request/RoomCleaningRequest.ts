import { RoomDetailsRequest } from "./RoomDetailsRequest";

export class RoomCleaningRequest {
    roomDetails!: RoomDetailsRequest;
    cleaningType!: string;
    cleaningDescription!: string;
  }