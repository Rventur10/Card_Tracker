import { Card } from './card';

export interface Price {
    priceId: number;
    psa10: number;
    psa9: number;
    nm: number;
    lp: number;
    mp: number;
    dmg: number;
    pullDate: string;  
    card: Card;
}