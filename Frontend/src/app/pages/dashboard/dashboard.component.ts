import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PokemonService, PokemonSet, PokemonCard } from '../../services/card.service';

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule], 
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {  
  title = 'Pokemon Card Dashboard';
  message = 'Loading sets...';
  sets: PokemonSet[] = [];
  loading = true;
  error: string | null = null;

  constructor(private pokemonService: PokemonService) { }

  ngOnInit() {
    this.pokemonService.getSets().subscribe({
      next: (sets) => {
        console.log('Got sets:', sets);
        this.sets = sets;
        this.message = `Loaded ${sets.length} sets successfully!`;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading sets:', err);
        this.error = 'Failed to load sets';
        this.message = 'Error loading sets';
        this.loading = false;
      }
    });
  }


  //placeholder
  loadCardsFromSet(setId: string) {
    this.pokemonService.getCardsBySet(setId).subscribe({
      next: (cards) => {
        console.log(`Got ${cards.length} cards from set ${setId}:`, cards);
      },
      error: (err) => {
        console.error(`Error loading cards from set ${setId}:`, err);
      }
    });
  }
}