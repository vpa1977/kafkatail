using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.Design;
using System.Diagnostics.CodeAnalysis;

namespace KafkaTail.Data
{
    public class KafkaTailDbContext : Microsoft.AspNetCore.Identity.EntityFrameworkCore.IdentityDbContext
    {
        public KafkaTailDbContext([NotNullAttribute] DbContextOptions options) : base(options)
        {
        }

        protected KafkaTailDbContext()
        {
        }

        protected override void OnModelCreating(ModelBuilder modelBuilder)
        {
            base.OnModelCreating(modelBuilder);
           
           // modelBuilder.ApplyConfigurationsFromAssembly(typeof().Assembly);
        }
    }

    public class KafkaTailDbContextFactory : IDesignTimeDbContextFactory<KafkaTailDbContext>
    {
        public KafkaTailDbContext CreateDbContext(string[] args)
        {
            var optionsBuilder = new DbContextOptionsBuilder<KafkaTailDbContext>();
            optionsBuilder.UseNpgsql("Host=localhost;Database=kafkatail;Username=postgres;Password=mysecretpassword");

            return new KafkaTailDbContext(optionsBuilder.Options);
        }
    }
}
