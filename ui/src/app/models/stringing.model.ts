export interface Stringing {
  stringingId: string;
  stringerUserId: string;
  ownerUserId: string;
  racketOwnerName: string;
  racketBrand: string;
  racketModel: string;
  stringName: string;
  stringTensionMains: number;
  stringTensionCrosses: number;
  stringingState: StringingState;
  requestedAt: string;
  receivedAt: string | null;
  startedAt: string | null;
  finishedAt: string | null;
  completedAt: string | null;
  failedAt: string | null;
  failedCompletedAt: string | null;
}

export type StringingState =
  | 'REQUESTED_BUT_NOT_DELIVERED'
  | 'RECEIVED_BUT_NOT_STARTED'
  | 'IN_PROGRESS'
  | 'FINISHED_BUT_NOT_PICKED_UP'
  | 'FAILED_BUT_NOT_PICKED_UP'
  | 'COMPLETED'
  | 'FAILED_COMPLETED';
