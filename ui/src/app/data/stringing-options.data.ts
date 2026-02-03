// Static data for stringing form dropdowns
// Hosted in UI because:
// 1. Data changes infrequently (racket models/strings don't change often)
// 2. No network latency - instant loading
// 3. Simpler architecture - no backend endpoint needed
// 4. Easy to update by editing this file
// If this data needed to be user-customizable or updated frequently, backend storage would be better

export const RACKET_MAKES = [
  'Yonex',
  'Victor',
  'Li-Ning',
  'Babolat',
  'Carlton',
  'Ashaway',
  'Apacs',
  'Fleet',
  'Dunlop',
  'Wilson',
  'Head',
  'Mizuno',
  'Forza',
  'Karakal',
  'Gosen'
];

export const RACKET_MODELS: Record<string, string[]> = {
  'Yonex': [
    'Astrox 88D Pro', 'Astrox 88S Pro', 'Astrox 99 Pro', 'Astrox 100ZZ',
    'Nanoflare 700', 'Nanoflare 800', 'Nanoflare 1000Z',
    'Arcsaber 11 Pro', 'Arcsaber 11', 'Arcsaber 7 Pro',
    'Duora 10', 'Duora Z-Strike',
    'Voltric Z-Force II', 'Voltric 80 E-Tune'
  ],
  'Victor': [
    'Thruster Ryuga', 'Thruster Ryuga II', 'Thruster F',
    'Jetspeed S 12', 'Jetspeed S 12F', 'Jetspeed S 10',
    'Auraspeed 90S', 'Auraspeed 100X',
    'DriveX 9X', 'DriveX 10',
    'Brave Sword 12'
  ],
  'Li-Ning': [
    'Axforce 80', 'Axforce 90', 'Axforce 100',
    'Bladex 900', 'Bladex 800',
    'Aeronaut 9000', 'Aeronaut 8000',
    'Halbertec 8000', 'Halbertec 9000',
    'Windstorm 72'
  ],
  'Babolat': [
    'Satelite Gravity 74', 'Satelite Blast',
    'X-Feel Origin', 'X-Feel Blast',
    'Satelite Touch'
  ],
  'Ashaway': [
    'Phantom X-Fire', 'Phantom X-Fire II',
    'Superlight 79SQ', 'Superlight 10 Hex'
  ],
  'Other': []
};

export const STRING_TYPES = [
  // Yonex
  'BG65', 'BG65 Ti', 'BG66 Ultimax', 'BG80', 'BG80 Power',
  'Aerobite', 'Aerobite Boost', 'Exbolt 63', 'Exbolt 65',
  'Nanogy 95', 'Nanogy 98', 'Nanogy 99',
  'BG Aerosonic', 'BG66 Force',
  // Victor
  'VBS-66N', 'VBS-68P', 'VBS-69N', 'VBS-70',
  // Li-Ning
  'No.1', 'No.5', 'No.7',
  // Ashaway
  'Zymax 62 Fire', 'Zymax 66 Fire', 'Zymax 69 Fire',
  'Dynamite 17', 'Rally 21',
  // Gosen
  'G-Tone 5', 'G-Tone 9',
  // Other
  'Nanogy 99 (Custom)', 'BG65 Power'
];

export const STRING_COLORS = [
  'White',
  'Black',
  'Yellow',
  'Orange',
  'Red',
  'Pink',
  'Blue',
  'Green',
  'Purple',
  'Gold',
  'Silver',
  'Neon Yellow',
  'Neon Green',
  'Sky Blue',
  'Navy Blue'
];

// Tension range for badminton rackets (in lbs)
export const TENSION_MIN = 18;
export const TENSION_MAX = 35;
export const TENSION_DEFAULT = 24;

// Common tension presets with athletic relevance naming
export const TENSION_PRESETS = [
  { label: 'Beginner', mains: 22, crosses: 22 },
  { label: 'Recreational', mains: 24, crosses: 24 },
  { label: 'Intermediate', mains: 26, crosses: 26 },
  { label: 'Advanced', mains: 28, crosses: 28 },
  { label: 'Professional', mains: 30, crosses: 30 }
];
