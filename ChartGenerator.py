import os
import json
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.lines import Line2D
from matplotlib.backends.backend_pdf import PdfPages

def load_validation_data(json_filename="./report/output_benchmark.json"):
    """Loads the JSON file and parses it into structured DataFrames."""
    if not os.path.exists(json_filename):
        print(f"Error: The file '{json_filename}' does not exist.")
        return None, None

    with open(json_filename, "r") as f:
        data = json.load(f)

    models_summary = []
    detailed_questions = []

    # Sort keys alphabetically to keep model families grouped together (e.g., GEMINI)
    for model_name, info in sorted(data.items()):
        models_summary.append({
            "Model": model_name,
            "Total_Accuracy": info["totalAccuracy"] * 100,
            "Avg_Response_Time_Sec": info["avgResponseTime"] / 1000.0
        })
        
        for q in info["questionsResult"]:
            # Categorize the accuracy of the single question for the stacked bar chart
            acc = q["accuracy"]
            if acc == 1.0:
                category = "Full Success (1.0)"
            elif acc == 0.0:
                category = "Total Failure (0.0)"
            else:
                category = "Partial Success (0.0 - 1.0)"

            detailed_questions.append({
                "Model": model_name,
                "Accuracy_Single": acc * 100,
                "Time_Sec": q["avgTime"] / 1000.0,
                "Outcome_Category": category
            })

    df_summary = pd.DataFrame(models_summary)
    df_detailed = pd.DataFrame(detailed_questions)

    return df_summary, df_detailed


def plot_accuracy_chart(pdf_worker, df_summary):
    """Page 1: Horizontal Bar Chart for Total Accuracy."""
    fig, ax = plt.subplots(figsize=(10, 6))
    
    bars = ax.barh(df_summary["Model"], df_summary["Total_Accuracy"], color='#2196F3', height=0.5)
    ax.invert_yaxis()  # Keep alphabetical order top-to-bottom
    
    # Append data labels to the end of each horizontal bar
    for bar in bars:
        xval = bar.get_width()
        ax.text(xval + 1, bar.get_y() + bar.get_height()/2.0, f"{xval:.1f}%", 
                ha='left', va='center', fontweight='bold', fontsize=10)

    ax.set_title("Overall Model Accuracy in Tool Usage", fontsize=14, fontweight="bold", pad=15)
    ax.set_xlabel("Accuracy (%)", fontsize=11)
    ax.set_xlim(0, 110)
    ax.grid(axis="x", linestyle="--", alpha=0.5)
    
    plt.tight_layout()
    pdf_worker.savefig(fig)
    plt.close(fig)


def plot_response_time_boxplot(pdf_worker, df_detailed):
    """Page 2: Horizontal Boxplot showing response time distribution with custom legend."""
    fig, ax = plt.subplots(figsize=(10, 6))
    
    df_detailed.boxplot(column="Time_Sec", by="Model", ax=ax, vert=False, grid=False,
                        patch_artist=True, 
                        boxprops=dict(facecolor="#e3f2fd", color="#2196F3"),
                        flierprops=dict(marker='o', markerfacecolor='#FF5722', markeredgecolor='#FF5722'),
                        medianprops=dict(color="#4CAF50", linewidth=2.5))
    
    ax.invert_yaxis()
    ax.set_title("Distribution of Response Times per Query", fontsize=14, fontweight="bold", pad=15)
    plt.suptitle("")  # Clear out the default pandas title layout
    ax.set_xlabel("Response Time (Seconds)", fontsize=11)
    ax.set_ylabel("Models", fontsize=11)
    ax.grid(axis="x", linestyle="--", alpha=0.5)
    
    # Custom components for the Boxplot Guide Legend
    box_patch = mpatches.Patch(facecolor='#e3f2fd', edgecolor='#2196F3', label='50% of Core Responses')
    median_line = Line2D([0], [0], color='w', marker='|', markeredgecolor='#4CAF50', markersize=14, markeredgewidth=3, label='Median (Central Value)')
    whiskers_line = Line2D([0], [0], color="#20659E", linestyle='-', marker='|', markersize=8, markeredgewidth=1.5, label='50% of remaining responses')
    outlier_dot = Line2D([0], [0], marker='o', color='w', markerfacecolor='#FF5722', markersize=8, label='Outliers (Unusually Long Queries)')
    
    ax.legend(handles=[box_patch, median_line, whiskers_line, outlier_dot], 
              loc='lower right', title="Boxplot Reading Guide", 
              title_fontproperties={'weight': 'bold'}, frameon=True, facecolor='white', edgecolor='gray')
    
    plt.tight_layout()
    pdf_worker.savefig(fig)
    plt.close(fig)


def plot_tradeoff_scatterplot(pdf_worker, df_summary):
    """Page 3: Scatter Plot illustrating the Efficiency vs Efficacy Trade-off with a side legend."""
    fig, ax = plt.subplots(figsize=(11, 6)) # Slightly wider layout to host the legend comfortably
    
    # Define a clean academic color palette for models (Matplotlib's tab10 offers 10 distinct colors)  
    colors = plt.colormaps['tab10'].colors
    
    legend_elements = []
    
    # Highlight the Optimal Area (High accuracy, low response times)
    ax.axhspan(80, 105, xmin=0, xmax=0.3, color='#e8f5e9', alpha=0.5, zorder=1)
    ax.text(df_summary['Avg_Response_Time_Sec'].min(), 103, "OPTIMAL ZONE\n(Fast & Highly Accurate)", 
             color='#2e7d32', fontsize=9, fontweight='bold', va='top')

    # Loop through models and plot each as a numbered, uniquely colored dot
    for idx, row in df_summary.iterrows():
        # Cycle through colors if there are more than 10 models
        color = colors[idx % len(colors)]
        model_number = str(idx + 1)
        
        # Plot the point
        ax.scatter(row['Avg_Response_Time_Sec'], row['Total_Accuracy'], 
                   s=250, color=color, alpha=0.9, edgecolors='black', zorder=3)
        
        # Add the index number directly inside or centered slightly above the dot
        ax.text(row['Avg_Response_Time_Sec'], row['Total_Accuracy'], model_number,
                fontsize=9, fontweight='bold', color='white', ha='center', va='center', zorder=4)
        
        # Create custom legend entry matching color and number to the full model name
        legend_elements.append(
            Line2D([0], [0], marker='o', color='w', markerfacecolor=color, markersize=10, 
                   markeredgecolor='black', label=f"[{model_number}] {row['Model']}")
        )
    
    ax.set_title("Trade-off Analysis: Accuracy vs. Response Time", fontsize=14, fontweight="bold", pad=15)
    ax.set_xlabel("Average Response Time (Seconds)", fontsize=11)
    ax.set_ylabel("Total Accuracy (%)", fontsize=11)
    
    ax.set_ylim(-5, 110)
    
    # Give some safe breathing room on the X-axis
    max_time = df_summary['Avg_Response_Time_Sec'].max()
    ax.set_xlim(-max_time * 0.05, max_time * 1.1)
    
    ax.grid(True, linestyle="--", alpha=0.5, zorder=2)
    
    # Place the model list legend OUTSIDE the chart layout on the right side
    ax.legend(handles=legend_elements, loc='center left', bbox_to_anchor=(1.02, 0.5),
              title="Tested Models", title_fontproperties={'weight': 'bold'}, frameon=True)

    # Adjust layout ensuring the external legend fits inside the saved PDF page bounding box
    plt.tight_layout()
    pdf_worker.savefig(fig, bbox_inches='tight')
    plt.close(fig)


def plot_consistency_stacked_bar(pdf_worker, df_detailed):
    """Page 4: Stacked Bar Chart evaluating structural consistency of model outcomes."""
    fig, ax = plt.subplots(figsize=(10, 6))
    
    # Group and pivot the categories to build absolute counts per model
    df_pivot = df_detailed.groupby(['Model', 'Outcome_Category']).size().unstack(fill_value=0)
    
    # Enforce order layout dynamically to make sure colors match logically
    categories_order = ["Full Success (1.0)", "Partial Success (0.0 - 1.0)", "Total Failure (0.0)"]
    df_pivot = df_pivot.reindex(columns=categories_order, fill_value=0)
    
    # Colors matching the layout: Green (Success), Orange (Partial), Red (Failure)
    colors = ['#4CAF50', '#FF9800', '#F44336']
    
    df_pivot.plot(kind='barh', stacked=True, ax=ax, color=colors, width=0.5)
    ax.invert_yaxis()
    
    ax.set_title("Accuracy per question", fontsize=14, fontweight="bold", pad=15)
    ax.set_xlabel("Number of Benchmark Questions", fontsize=11)
    ax.set_ylabel("Models", fontsize=11)
    ax.grid(axis="x", linestyle="--", alpha=0.5)
    ax.legend(title="Execution Outcomes", loc='lower right')
    
    plt.tight_layout()
    pdf_worker.savefig(fig)
    plt.close(fig)


def main():
    json_filename = "./report/output_benchmark.json"
    pdf_filename = "./report/output_benchmark_chart.pdf"
    
    # Load and clean dataset pipeline
    df_summary, df_detailed = load_validation_data(json_filename)
    if df_summary is None:
        return

    try:
        print("Processing charts and generating multipage PDF...")
        # Initialize multipage PDF pipeline context
        with PdfPages(pdf_filename) as pdf:
            plot_accuracy_chart(pdf, df_summary)
            plot_response_time_boxplot(pdf, df_detailed)
            plot_tradeoff_scatterplot(pdf, df_summary)
            plot_consistency_stacked_bar(pdf, df_detailed)
            
        print(f"Execution finished! Complete report saved as: '{pdf_filename}'")
        
    except Exception as e:
        print(f"An error occurred during plot generations: {e}")

if __name__ == "__main__":
    main()